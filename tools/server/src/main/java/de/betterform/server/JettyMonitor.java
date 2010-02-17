package de.betterform.server;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Lars Windauer
 */

public class JettyMonitor extends JFrame implements ActionListener {

    private JLabel jLabelWelcome;
    private JLabel jLabelServerOutLabel;
    private JLabel jLabelServerOut;

    static final long serialVersionUID = 1L;
    private static JettyMonitor instance;
    private JettyCtrl jetty;
    

    private JettyMonitor() throws Exception {
        jetty = JettyCtrl.getInstance();
        jetty.start();

        this.setSize(new Dimension(300, 150));
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width - this.getWidth()) / 2, (d.height - this.getHeight()) / 2);
        this.setTitle(JettyProperties.TITLE);
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

    public static JettyMonitor getInstance() throws Exception {
        if (instance == null) {
            return instance = new JettyMonitor();
        } else {
            return instance;
        }
    }

    private void createContentPanel() {
        JPanel jP = new JPanel();
        jP.setLayout(new BorderLayout());
        getContentPane().add(jP);
        jLabelWelcome = new JLabel(JettyProperties.WELCOME);
        jP.add(BorderLayout.NORTH, jLabelWelcome);
        JPanel centerPanel = new JPanel();
        JButton jBtnStartBrowser = new JButton(JettyProperties.START_BROWSER);
        jBtnStartBrowser.addActionListener(this);
        jBtnStartBrowser.setEnabled(true);
        centerPanel.add(jBtnStartBrowser);
        jP.add(BorderLayout.CENTER, centerPanel);
        jLabelServerOutLabel = new JLabel(JettyProperties.SERVER_STATUS_LABEL);
        jLabelServerOut = new JLabel(jetty.getStatus());
        JPanel southPanel = new JPanel();
        southPanel.add(jLabelServerOutLabel);
        southPanel.add(jLabelServerOut);
        jP.add(BorderLayout.SOUTH, southPanel);
        setJMenuBar(createMenu());
    }

    private JMenuBar createMenu() {
        JMenuBar jMnuB = new JMenuBar();
        JMenu jMnuRiscServer = new JMenu(JettyProperties.MENU_TITLE);
        jMnuB.add(jMnuRiscServer);
        JMenuItem jMnuI5 = new JMenuItem(JettyProperties.MENU_QUIT, 'Q');
        jMnuI5.addActionListener(this);
        jMnuRiscServer.add(jMnuI5);
        JMenu jMnuWindow = new JMenu("Window");
        jMnuB.add(jMnuWindow);
        JMenu JMnuLookAndFeel = new JMenu("Look & Feel");
        jMnuWindow.add(JMnuLookAndFeel);
        JRadioButtonMenuItem jMnuIMetal = new JRadioButtonMenuItem(JettyProperties.MENU_METAL, true);
        jMnuIMetal.addActionListener(this);
        JMnuLookAndFeel.add(jMnuIMetal);
        JRadioButtonMenuItem jMnuIWindow = new JRadioButtonMenuItem(JettyProperties.MENU_WINDOW);
        jMnuIWindow.addActionListener(this);
        JMnuLookAndFeel.add(jMnuIWindow);
        JRadioButtonMenuItem jMnuIMotif = new JRadioButtonMenuItem(JettyProperties.MENU_MOTIF);
        jMnuIMotif.addActionListener(this);
        JMnuLookAndFeel.add(jMnuIMotif);
        ButtonGroup bg11 = new ButtonGroup();
        bg11.add(jMnuIMetal);
        bg11.add(jMnuIWindow);
        bg11.add(jMnuIMotif);
        return jMnuB;
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() instanceof JRadioButtonMenuItem || ae.getSource() instanceof JButton ||
                ae.getSource() instanceof JMenuItem) {
            if (ae.getActionCommand().equals(JettyProperties.MENU_METAL)) {
                setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } else if (ae.getActionCommand().equals(JettyProperties.MENU_WINDOW)) {
                setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } else if (ae.getActionCommand().equals(JettyProperties.MENU_MOTIF)) {
                setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            } else if (ae.getActionCommand().equals(JettyProperties.START_BROWSER)) {
                System.out.println("Start Browser");
                startBrowser();
            } else if (ae.getActionCommand().equals(JettyProperties.MENU_QUIT)) {
                System.exit(0);
            }
        }
    }

    private void setLookAndFeel(String laf) {
        try {
            UIManager.setLookAndFeel(laf);
        } catch (Exception ex) {
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void startBrowser() {
        Thread launcherThread;
        String urlString = "http://localhost:" + JettyProperties.getPort();
        try {
            BrowserLauncher launcher = new BrowserLauncher(null);
            launcher.openURLinBrowser(urlString);

        } catch (BrowserLaunchingInitializingException e2) {
            e2.printStackTrace();
        } catch (UnsupportedOperatingSystemException e2) {
            e2.printStackTrace();
        }
    }
}
