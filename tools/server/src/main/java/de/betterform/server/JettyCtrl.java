package de.betterform.server;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.NCSARequestLog;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;
import org.mortbay.thread.ThreadPool;
import org.w3c.dom.Document;
import org.apache.commons.jxpath.JXPathContext;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

public class JettyCtrl {
    private Server server;
    private static JettyCtrl instance;
    static final String STATUS_RUNNING = "running";
    static final String STATUS_STOPPED = "stopped";
    private String status = STATUS_STOPPED;
    private static int PORT = 45898;
    private String WORKING_DIRECTORY;


    private JettyCtrl() {
        this.server = new Server();
        WORKING_DIRECTORY = new File(".").getAbsolutePath();
        this.server.setAttribute("JETTY_HOME", WORKING_DIRECTORY);

        try {
            String pathToConfig = new File(WORKING_DIRECTORY + "/bin/server-conf.xml").getAbsolutePath();
            // create DOM of XML
            Document config = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pathToConfig);

            // grep port from config
            JXPathContext jxContext = JXPathContext.newContext(config);
            PORT = Integer.parseInt(jxContext.getValue("/server/port").toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        ThreadPool threadPool = new QueuedThreadPool();
        server.setThreadPool(threadPool);

        //setup realm
/*
        JAASUserRealm realm = new JAASUserRealm(JettyProperties.JAAS_USER_REALM);
        realm.setLoginModuleName(JettyProperties.LOGIN_MODULE_NAME);
        server.addUserRealm(realm);
*/

        Connector connector = new SelectChannelConnector();
        connector.setPort(PORT);

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
            this.status = STATUS_RUNNING;
        }

    }

    public void stop() throws Exception {
        server.stop();
        this.status = STATUS_STOPPED;
    }

    public String getStatus() {
        return this.status;
    }

    public boolean isRunning() {
        return server.isRunning();
    }

    public int getPort() {
        return PORT;
    }
    public String getWorkDir() {
        return WORKING_DIRECTORY;
    }
}
