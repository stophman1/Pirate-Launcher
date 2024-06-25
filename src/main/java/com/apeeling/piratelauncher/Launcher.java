package com.apeeling.piratelauncher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class Launcher extends JFrame{
    private JPanel mainPanel;
    private JPasswordField PasswordBox;
    private JComboBox comboBox1;
    private JButton playButton;
    private JTextField UsernameBox;
    private JLabel gameVersion;
    private JLabel fileName;
    private JProgressBar downloadBar;

    public Launcher(String title) throws IOException {
        super(title);
        Dimension objDimension = Toolkit.getDefaultToolkit().getScreenSize();
        int iCoordX = (objDimension.width - this.getWidth()) / 2;
        int iCoordY = (objDimension.height - this.getHeight()) / 2;
        this.setLocation(iCoordX, iCoordY);
        fileName.setVisible(false);
        downloadBar.setVisible(false);
        RequestManager rqman = new RequestManager();
        gameVersion.setText(rqman.GetVersion());
        //comboBox1.addItem(new ComboItem("Linux", "linux2"));
        //comboBox1.addItem(new ComboItem("Mac", "mac"));
        //comboBox1.addItem(new ComboItem("Windows 64-bit", "win64"));
        comboBox1.addItem(new ComboItem("Live", "download"));
        comboBox1.addItem(new ComboItem("Test Server", "download-test"));
        comboBox1.addItem(new ComboItem("Dev (QA)", "download-dev"));

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.setResizable(false);
        this.setSize(500, 250);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileName.setVisible(true);
                fileName.setText("Downloading");
                RequestManager rqman = new RequestManager();
                Object item = comboBox1.getSelectedItem();
                String value = ((ComboItem)item).getValue();
                Thread download = new Thread(() -> {
                    try {
                        rqman.DownloadMan(value);
                    } catch (IOException | NoSuchAlgorithmException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                download.start();
                new Thread(() -> {
                    try {
                        download.join();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    fileName.setText("Launching Game");
                    try {
                        Object[] myCoolArray = rqman.login(UsernameBox.getText(), PasswordBox.getPassword());
                        if (!(myCoolArray == null)) {
                            launchgame(myCoolArray[2].toString(), myCoolArray[3].toString(), rqman.detectOS(), rqman.GetExec(value));
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }).start();

            }
        });
    }

    public void launchgame(String cookie, String server, String os, String exec){
        Thread launch = new Thread(new Runnable() {
            @Override
            public void run() {
                if(cookie==null && server == null) return;
                ProcessBuilder builder = new ProcessBuilder();
                if(os.equals("win64")) builder.command(exec);
                if(os.equals("mac")) builder.command(exec+".app");
                if(os.equals("linux2")) {
                    boolean makeExec = new File(exec).setExecutable(true);
                    if(!makeExec) {
                        ErrorWindow dialog = new ErrorWindow("Unix Error: Can not mark "+exec+" as executable!");
                        dialog.pack();
                        dialog.setVisible(true);
                        return;
                    }
                    builder.command("./"+exec);
                }

                builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
                builder.redirectErrorStream(true);
                Map<String, String> envVar = builder.environment();
                envVar.put("TLOPO_GAMESERVER",server);
                envVar.put("TLOPO_PLAYCOOKIE",cookie);

                Thread thr = new Thread( () -> {
                    try {
                        Process proc = builder.start();
                        proc.getInputStream().close();
                        proc.waitFor();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                thr.start();
            }
        });
        launch.start();
    }



    public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        JFrame frame = new Launcher("pirate carabean free toolbar cracked offline 2020");
        frame.setVisible(true);
    }
}

class ComboItem
{
    private String key;
    private String value;

    public ComboItem(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return key;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}