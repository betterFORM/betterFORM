package de.betterform.server;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import org.mortbay.log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

/**
 * @author Lars Windauer
 */

public class BfDialog extends JFrame implements ActionListener {

    private JettyCtrl jetty = JettyCtrl.getInstance();
    private static BfDialog instance;
    static final String ACTION_COMMAND = "Start Browser";

    // static final Dimension WINDOW_SIZE = new Dimension();

    // b_tryit.png                  : 290, 150
    // betterform_icon_118x118.png  : 120, 150
    static final Dimension WINDOW_SIZE = new Dimension(120, 150);
    private static final String TRIGGER_ICON = "betterform_icon_118x118.png";
    private static final String TRIGGER_ROLLOVER_ICON = "betterform_icon_118x118-RollOver.png";

/*
    static final String TITLE = "betterFORM Server 1.0";

    static final String SERVER_STATUS_LABEL = "Server status:";
*/

    public static BfDialog getInstance() throws Exception {
        if (instance == null) {
            return instance = new BfDialog();
        } else {
            return instance;
        }
    }

    /*


     */
    private BfDialog() throws Exception {
        jetty.start();
        this.setSize(WINDOW_SIZE);

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width - this.getWidth()) / 2, (d.height - this.getHeight()) / 2);
        // this.setTitle(TITLE);

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (jetty.isRunning()) {
                    try {
                        jetty.stop();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });
        createContentPanel();
    }


    private void createContentPanel() throws IOException {
        JPanel centerPanel = new JPanel();
        URL triggerImageURL = new URL("file:"+jetty.getWorkDir().substring(0,jetty.getWorkDir().length()-1) + "images/" + TRIGGER_ICON);
        System.out.println("URL " + triggerImageURL);

        SystemTray tray = SystemTray.getSystemTray();
         Image image = Toolkit.getDefaultToolkit().getImage(triggerImageURL);

        JButton jBtnStartBrowser = new JButton();

        ImageIcon buttonImage = new ImageIcon(triggerImageURL);
        jBtnStartBrowser.setIcon(buttonImage);

        URL triggerRollOverImageURL = new URL("file:"+jetty.getWorkDir().substring(0,jetty.getWorkDir().length()-1) + "images/" + TRIGGER_ROLLOVER_ICON);
        ImageIcon rolloverImage = new ImageIcon(triggerRollOverImageURL);
        jBtnStartBrowser.setRolloverIcon(rolloverImage);

        jBtnStartBrowser.setActionCommand(ACTION_COMMAND);
        jBtnStartBrowser.setBorder(BorderFactory.createEmptyBorder());
        jBtnStartBrowser.setHorizontalAlignment(SwingConstants.CENTER);
        jBtnStartBrowser.setVerticalAlignment(SwingConstants.CENTER);


        jBtnStartBrowser.addActionListener(this);
        jBtnStartBrowser.setEnabled(true);
        centerPanel.add(jBtnStartBrowser);
        getContentPane().add(centerPanel);
        //jP.add(BorderLayout.CENTER, centerPanel);



        // JLabel jLabelServerOutLabel = new JLabel(SERVER_STATUS_LABEL);
        // JLabel jLabelServerOut = new JLabel(jetty.getStatus());
        // JPanel southPanel = new JPanel();
        // southPanel.add(jLabelServerOutLabel);
        // southPanel.add(jLabelServerOut);
        // jP.add(BorderLayout.SOUTH, southPanel);
        // setJMenuBar(createMenu());
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals(ACTION_COMMAND)) {
            startBrowser();
        }
    }

    private void startBrowser() {
        try {
            BrowserLauncher launcher = new BrowserLauncher(null);
            launcher.openURLinBrowser("http://localhost:" + jetty.getPort());

        } catch (BrowserLaunchingInitializingException e2) {
            e2.printStackTrace();
        } catch (UnsupportedOperatingSystemException e2) {
            e2.printStackTrace();
        }
    }
}
