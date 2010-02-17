package de.betterform.server;

import java.io.File;

/**
 * @author Lars Windauer
 */
  
public class JettyProperties {
    static final String LOGIN_MODULE_NAME = "BetterFORMLogin";
    static final String JAAS_USER_REALM = "betterform";
    //TODO: make configurable

    
    static final String TITLE = "betterFORM Server 1.0";
    static final String WELCOME = "Welcome to betterFORM Server 1.0";
    static final String START_BROWSER = "Start Browser";

    static final String MENU_TITLE = "betterFORM";
    
    static final String MENU_QUIT = "Quit";
    static final String MENU_MOTIF = "Motif";
    static final String MENU_METAL = "Metal";
    static final String MENU_WINDOW = "Window";

    static final String STATUS_STARTED = "started";
    static final String STATUS_STOPPED = "stopped";

    static final String SERVER_STATUS_LABEL = "Server status:";

    private static int PORT = 45898;

    public static int getPort() {
        return PORT;
    }

    public static void setPort(int port) {
        PORT = port;
    }
}
