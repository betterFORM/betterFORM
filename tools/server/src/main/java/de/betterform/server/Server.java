package de.betterform.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Lars Windauer
 */
public class Server {
    private static final String EXIT = "OK";
    private static final String SERVICERUNNING = "Service is already running";


    public static void main(String[] args) {
        JettyMonitor jettyMonitor = null;
        try {
            jettyMonitor = JettyMonitor.getInstance();
            jettyMonitor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jettyMonitor.setVisible(true);
        } catch (Exception e) {
            JDialog dialog = new JDialog();
            dialog.setSize(300, 300);
            dialog.setLocation(300, 300);
            JLabel errorTitle = new JLabel();

            System.out.println("1: " + e.toString());
            System.out.println("2: " + e.getMessage());

            if (e instanceof java.net.BindException) {
                errorTitle.setText("\n\n" + SERVICERUNNING);
            } else if (e instanceof java.lang.IllegalArgumentException) {
                errorTitle.setText("\n\n" + EXIT);
            } else {
                errorTitle.setText("Unknown Error: " + e.toString());
            }
            JPanel jp = new JPanel();
            JButton btnExit = new JButton(EXIT);
            jp.add(btnExit);

            btnExit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    if (ae.getActionCommand().equals(EXIT)) {
                        System.exit(0);
                    }
                }
            });
            dialog.setLayout(new BorderLayout());
            dialog.add(BorderLayout.CENTER, jp);
            dialog.add(BorderLayout.NORTH, errorTitle);
            dialog.setVisible(true);
        }
    }
}
