package de.betterform.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Lars Windauer
 */
public class Server extends Frame implements ActionListener {

    private static final String EXIT = "OK";
    private static final String SERVICERUNNING = "Service is already running";
    

    public static void main(String[] args) {

        try {
            showSplitScreen();
            BfDialog bfDialog = null;
            bfDialog = bfDialog.getInstance();
            bfDialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            showSplitScreen();
            bfDialog.setVisible(true);
            bfDialog.toFront();
        }
		catch (Exception e) {
    		handleExecption(e);
        }
    }


    private static void showSplitScreen() {
        SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            return;
        }
        Graphics2D g = splash.createGraphics();
        if (g == null) {
            System.out.println("g is null");
            return;
        }
        for(int i=0; i<10; i++) {
            renderSplashFrame(g, i);
            splash.update();
            try {
                Thread.sleep(50);
            }
            catch(InterruptedException ie) {
            }
        }
        splash.close();

    }
    private static void handleExecption(Exception e) {
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


    }
    static void renderSplashFrame(Graphics2D g, int frame) {
        final String[] comps = {"betterFORM Core", "betterFORM Web", "betterFORM Demo Forms"};
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(120,140,200,40);
        g.setPaintMode();
        g.setColor(Color.BLACK);
        g.drawString("Loading "+comps[(frame/5)%3]+"...", 120, 150);
    }

    public void actionPerformed(ActionEvent ae) {
        System.exit(0);
    }
    
}
