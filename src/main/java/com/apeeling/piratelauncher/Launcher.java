package com.apeeling.piratelauncher;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;

public class Launcher extends JFrame {
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
                String value = ((ComboItem) item).getValue();
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

    public void launchgame(String cookie, String server, String os, String exec) {
        Thread launch = new Thread(new Runnable() {
            @Override
            public void run() {
                if (cookie == null && server == null) return;
                ProcessBuilder builder = new ProcessBuilder();
                if (os.equals("win64")) builder.command(exec);
                if (os.equals("linux2") || os.equals("mac")) {
                    boolean makeExec = new File(exec).setExecutable(true);
                    if (!makeExec) {
                        ErrorWindow dialog = new ErrorWindow("Unix Error: Can not mark " + exec + " as executable!");
                        dialog.pack();
                        dialog.setVisible(true);
                        return;
                    }
                    builder.command("./" + exec);
                }

                builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
                builder.redirectErrorStream(true);
                Map<String, String> envVar = builder.environment();
                envVar.put("TLOPO_GAMESERVER", server);
                envVar.put("TLOPO_PLAYCOOKIE", cookie);

                Thread thr = new Thread(() -> {
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(8, 5, new Insets(0, 0, 0, 0), -1, -1));
        playButton = new JButton();
        playButton.setText("Play");
        mainPanel.add(playButton, new GridConstraints(4, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(150, -1), null, 0, false));
        PasswordBox = new JPasswordField();
        mainPanel.add(PasswordBox, new GridConstraints(3, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Password");
        mainPanel.add(label1, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Username");
        mainPanel.add(label2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        UsernameBox = new JTextField();
        UsernameBox.setText("");
        mainPanel.add(UsernameBox, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(150, -1), null, 0, false));
        comboBox1 = new JComboBox();
        comboBox1.setEditable(false);
        comboBox1.setEnabled(true);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        comboBox1.setModel(defaultComboBoxModel1);
        mainPanel.add(comboBox1, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(7, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(0, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        gameVersion = new JLabel();
        gameVersion.setEnabled(true);
        Font gameVersionFont = this.$$$getFont$$$(null, -1, -1, gameVersion.getFont());
        if (gameVersionFont != null) gameVersion.setFont(gameVersionFont);
        gameVersion.setHorizontalAlignment(10);
        gameVersion.setText("Game Version");
        mainPanel.add(gameVersion, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("");
        mainPanel.add(label3, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        mainPanel.add(spacer3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        mainPanel.add(spacer4, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        downloadBar = new JProgressBar();
        downloadBar.setString("0%");
        downloadBar.setValue(0);
        mainPanel.add(downloadBar, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fileName = new JLabel();
        fileName.setText("Placeholder");
        fileName.setVisible(true);
        mainPanel.add(fileName, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
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