package com.apeeling.piratelauncher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ErrorWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel errorMsg;
    private JButton buttonCancel;

    public ErrorWindow(String theError) {
        Dimension objDimension = Toolkit.getDefaultToolkit().getScreenSize();
        int iCoordX = (objDimension.width - this.getWidth()) / 2;
        int iCoordY = (objDimension.height - this.getHeight()) / 2;
        this.setLocation(iCoordX, iCoordY);

        errorMsg.setText(theError);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }

    private void onOK() {
        // add your code here
        dispose();
    }


    public static void main(String[] args) {
        ErrorWindow dialog = new ErrorWindow("Works");
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
