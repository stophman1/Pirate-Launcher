package com.apeeling.piratelauncher;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

import com.google.gson.*;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public class RequestManager {
    public String GetVersion() throws IOException {
        String webPage = "https://api.tlopo.com/releases/feed";

        try (InputStream is = new URL(webPage).openStream();
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            Version[] versionArray = gson.fromJson(reader, Version[].class);
            return versionArray[0].toString();
        }
    }

    public String GetExec(String server) throws IOException { //Used for launching
        String osUsed = detectOS();
        String patcherURL = String.format("https://%s.tlopo.com/%s/patcher.json", server, osUsed);

        try (InputStream is = new URL(patcherURL).openStream();
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            Exec exec = gson.fromJson(reader, Exec.class);
            return exec.toString();
        }
    }


    public Map<String, Object> GetFiles(String server) throws IOException {
        String osUsed = detectOS();
        String patcherURL = String.format("https://%s.tlopo.com/%s/patcher.json", server, osUsed);

        try (InputStream is = new URL(patcherURL).openStream();
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            Map<String, Map<String, Object>> theMap;
            theMap = gson.fromJson(reader, Map.class);
            return theMap.get("files");
        }


    }


    public void DownloadMan(String server) throws IOException, NoSuchAlgorithmException, InterruptedException {
        String osUsed = detectOS();
        if (osUsed.equals("unsupported")) {
            ErrorWindow dialog = new ErrorWindow("Your OS is not supported by this launcher.");
            dialog.pack();
            dialog.setVisible(true);
            return;
        }
        Map<String, Object> curFile = GetFiles(server);
        Object[] leArray = curFile.keySet().toArray();
        class RunnableClass implements Runnable{
            Object obj;

            public RunnableClass(Object o) {
                this.obj = o;
            }

            public void run() {
                try {
                    downFile(osUsed,
                            curFile.get(obj).toString().substring(1, curFile.get(obj).toString().length() - 1).split(", ")[0].substring(5),
                            (String) obj,
                            server,
                            curFile.get(obj).toString().substring(1, curFile.get(obj).toString().length() - 1).split(", ")[1].substring(5));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        ExecutorService executor= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        int i = 0;
        for (Object o : leArray) {
            i++;
            executor.execute(new RunnableClass(o));
            }
        executor.shutdown();
        executor.awaitTermination(15, TimeUnit.MINUTES);
        if(detectOS().equals("linux2")){ //Temporary hack because executable is missing in the patcher.json
            downFile("linux2", "", "tlopo", server, null);
        }
            //Inform the user when they can play
            //ErrorWindow dialog = new ErrorWindow("Download complete. :- ]");
            //dialog.pack();
            //dialog.setVisible(true);
    }

    public void downFile(String osUsed, String filePath, String file, String server, String hash) throws IOException, NoSuchAlgorithmException {
        if (hashCompare(file, server, hash)) {
            return;
        }
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        String downloadURL = String.format("https://%s.tlopo.com/%s/%s.bz2", server, osUsed, file);
        System.out.println(downloadURL);
        new File(filePath).mkdirs();
        try (BZip2CompressorInputStream inputStream = new BZip2CompressorInputStream(new BufferedInputStream(new URL(downloadURL).openStream()));
             FileOutputStream fileOut = new FileOutputStream(file))
        {
            byte[] data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOut.write(data, 0, byteContent);
            }
        }
    }

    public String hashCheck(String file) throws IOException, NoSuchAlgorithmException {
        FileInputStream inputStream = new FileInputStream(file);
        byte[] data = new byte[1024];
        int byteContent;
        MessageDigest digest = MessageDigest.getInstance("SHA256");
        while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
            digest.update(data, 0, byteContent);
        }
        inputStream.close();
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.substring(0, sb.toString().length() - 1);
    }

    public boolean hashCompare(String file, String Server, String hash) throws IOException, NoSuchAlgorithmException {
        //String hash = hashCheck(file);
        File dafile = new File(file);
        //System.out.println("hashCheck: "+hashCheck(file));
        //System.out.println("hash: "+hash);
        return dafile.exists() && hashCheck(file).equals(hash);
    }

    public String detectOS() {
        //linux2 win64 and mac are supported by TLOPO
        String osUsed = null;
        if (SystemUtils.IS_OS_LINUX) {
            if (System.getProperty("os.arch").equalsIgnoreCase("amd64")) osUsed = "linux2";
        }
        else if (SystemUtils.IS_OS_WINDOWS) {
            if (System.getProperty("os.arch").equalsIgnoreCase("amd64")) osUsed = "win64";
        }
        else if (SystemUtils.IS_OS_MAC_OSX) {
            if (System.getProperty("os.arch").equalsIgnoreCase("amd64")) osUsed = "mac";
        }
        else osUsed = "unsupported";
        return osUsed;
    }

    public Object[] login(String username, char[] password) throws IOException { //todo make this better including proper security of passwords
        URL url = new URL("https://api.tlopo.com/login/");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", String.valueOf(password));
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : request.entrySet())
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
            os.flush();
            os.close();
            BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream()));
            Gson gson = new Gson();
            Map theMap = gson.fromJson(input, Map.class);
            Double status = new Double(theMap.values().toArray()[0].toString());
            if (!status.equals(7.0) && !status.equals(3.0))
            {
                ErrorWindow dialog = new ErrorWindow(theMap.values().toArray()[1].toString());
                dialog.pack();
                dialog.setVisible(true);
                return null;
            }
            if(status.equals(3.0))
            {
                ErrorWindow dialog = new ErrorWindow(theMap.values().toArray()[1].toString()+"Not Implemented Yet. :(");
                dialog.pack();
                dialog.setVisible(true);
                return null;
            }
            return theMap.values().toArray();
        }
    }
}

class Version {
    String version;
    @Override
    public String toString() {
        return version;
}
}

class Exec {
    private String exec;
    @Override
    public String toString() {
        return exec;
    }
}