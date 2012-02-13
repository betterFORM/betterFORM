/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.installer.utils;

import org.exist.security.User;
import org.exist.xmldb.DatabaseInstanceManager;
import org.exist.xmldb.UserManagementService;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
/**
 * @author <a href="mailto:tobias.krebs@betterform.de">tobi</a>
 * @version $Id: SetupBetterFORMUser 26.11.10 tobi $
 */
public class SetupBetterFORMUser {

    public static void main(String[] args) {
        String adminPasswd = "";

        if (args.length > 1 && args[0].length() > 0) {
            adminPasswd = args[0];
        }

        try {
            setupBetterFORMUser(adminPasswd);
        } catch (Exception e) {
            System.err.println("Could not create betterFORM user");
            e.printStackTrace();
        }
    }

    private static void setupBetterFORMUser(final String adminPasswd) throws Exception {
        final String betterFORM = "betterFORM";
        final String passwd = "Tha0xeiC8a";
        final String admin = "admin";
        final String dbUri = "xmldb:exist://localhost:8080/exist/xmlrpc/db";

        String driver = "org.exist.xmldb.DatabaseImpl";
        // initialize database driver
        Class cl = Class.forName(driver);
        Database database = (Database) cl.newInstance();
        DatabaseManager.registerDatabase(database);

        Collection db = DatabaseManager.getCollection(dbUri, admin, adminPasswd);
        UserManagementService userManagementService = (UserManagementService) db.getService("UserManagementService", "1.0");
        userManagementService.addUser(new User(betterFORM, passwd, betterFORM));
        Collection root = DatabaseManager.getCollection(dbUri, admin, adminPasswd);
        DatabaseInstanceManager manager = (DatabaseInstanceManager) root.getService("DatabaseInstanceManager", "1.0");
        manager.shutdown();
    }
}