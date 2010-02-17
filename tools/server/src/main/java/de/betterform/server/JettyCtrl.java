package de.betterform.server;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.NCSARequestLog;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.deployer.WebAppDeployer;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.BoundedThreadPool;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.apache.commons.jxpath.JXPathContext;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class JettyCtrl {
    private Server server;
    private static JettyCtrl instance;
    private String status = JettyProperties.STATUS_STOPPED;


    public static void main(String[] args) throws IOException {
/*
        try {
            JettyCtrl.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        String home = new File(".").getAbsolutePath();
        System.out.println("Path " + home);
        //File loginConf = new File(home + "/bin/login.conf");

    }

    private static String readFileAsString(String filePath)
       throws java.io.IOException{
           StringBuffer fileData = new StringBuffer(1000);
           return fileData.toString();
       }


    private JettyCtrl() {
        this.server = new Server();

        //todo: make dynamic
        //String home = "/Users/jinx/Desktop/risk-web/deploy/standalone/risk-web";
        String home = new File(".").getAbsolutePath();
        this.server.setAttribute("JETTY_HOME", home);
/*
        File loginConf = new File(home + "/bin/login.conf");
        System.setProperty("java.security.auth.login.config", loginConf.getAbsolutePath());
*/

        int port;
        try {
            String configFile = new File(home + "/web/root/WEB-INF/dbConfig.xml").getAbsolutePath();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            builder = factory.newDocumentBuilder();
            Document configDoc = null;
            configDoc = builder.parse(configFile);
            JXPathContext jxContext = JXPathContext.newContext(configDoc);
            port = Integer.parseInt(jxContext.getValue("/dbconfig/databases/database[1]/@port").toString());
            System.out.println("Server Port: " + port);
            JettyProperties.setPort(port);
        } catch (IOException e) {
            System.out.println("Error receiving Server Port from config. Port is now: " + JettyProperties.getPort());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace(); 
        }


        BoundedThreadPool threadPool = new BoundedThreadPool();
        threadPool.setMaxThreads(100);
        server.setThreadPool(threadPool);

        //setup realm
/*
        JAASUserRealm realm = new JAASUserRealm(JettyProperties.JAAS_USER_REALM);
        realm.setLoginModuleName(JettyProperties.LOGIN_MODULE_NAME);
        server.addUserRealm(realm);
*/

        Connector connector = new SelectChannelConnector();
        connector.setPort(JettyProperties.getPort());

        server.setConnectors(new Connector[]{connector});

        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();

        RequestLogHandler requestLogHandler = new RequestLogHandler();
        handlers.setHandlers(new Handler[]{contexts, new DefaultHandler(), requestLogHandler});
        server.setHandler(handlers);


        try {
            WebAppContext.addWebApplications(server, "web", "org/mortbay/jetty/webapp/webdefault.xml", true, false);            

        } catch (IOException e) {
            e.printStackTrace();
        }

        NCSARequestLog requestLog = new NCSARequestLog("./logs/jetty.log");
        requestLog.setExtended(false);
        requestLogHandler.setRequestLog(requestLog);

    }

    public static JettyCtrl getInstance() {
        if (instance == null) {
            return instance = new JettyCtrl();
        } else {
            return instance;
        }
    }


    public void start()  {

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();  
        }
        server.setStopAtShutdown(true);
        server.setSendServerVersion(true);

        if (server.isStarted()) {
            this.status = JettyProperties.STATUS_STARTED;
        }

    }

    public void stop() throws Exception {
        server.stop();
        this.status = JettyProperties.STATUS_STOPPED;
    }

    public String getStatus() {
        return this.status;
    }

    public boolean isRunning() {
        return server.isRunning();
    }

    public boolean isStarted() {
        return server.isStarted();
    }
}
