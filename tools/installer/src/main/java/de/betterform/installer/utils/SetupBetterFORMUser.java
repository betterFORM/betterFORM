/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.installer.utils;

import org.exist.security.Permission;
import org.exist.security.internal.aider.GroupAider;
import org.exist.security.internal.aider.UserAider;
import org.exist.security.internal.aider.PermissionAider;
import org.exist.security.internal.aider.PermissionAiderFactory;
import org.exist.xmldb.DatabaseInstanceManager;
import org.exist.xmldb.UserManagementService;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.modules.CollectionManagementService;
import org.exist.security.ACLPermission;

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

        //Create betterFORM group
        GroupAider group = new GroupAider(betterFORM);
        userManagementService.addGroup(group);

        //Create betterFORM user
        UserAider user = new UserAider(betterFORM);
        user.setPassword(passwd);
        user.addGroup(group);
        userManagementService.addAccount(user);

        Collection root = DatabaseManager.getCollection(dbUri, admin, adminPasswd);
        //Create betterFORM collection
        CollectionManagementService collectionManagementService = (CollectionManagementService) root.getService("CollectionManagementService", "1.0");
        Collection betterFORMCollection = collectionManagementService.createCollection(betterFORM);


        //Set owner and permissions
        PermissionAider permissionAider;
        Permission permissions = userManagementService.getPermissions(betterFORMCollection);
        /*
        System.err.println("---------------------------------");
        System.err.println("Owner:" + permissions.getOwner());
        System.err.println("Group:" + permissions.getGroup());
        System.err.println("---------------------------------");
        */
        permissions.setOwner(betterFORM);
        permissions.setGroup(betterFORM);
        permissions.setSticky(true);
        //permissions.setOwnerMode(new Integer(7));
        //permissions.setGroupMode(new Integer(5));
        //permissions.setOtherMode(new Integer(5));
        userManagementService.setPermissions(betterFORMCollection, permissions);
        /*
        System.err.println("---------------------------------");
        System.err.println("Owner:" + permissions.getOwner());
        System.err.println("Group:" + permissions.getGroup());
        System.err.println("---------------------------------");
        */
        betterFORMCollection.close();

        //Shutdown DB
        DatabaseInstanceManager manager = (DatabaseInstanceManager) root.getService("DatabaseInstanceManager", "1.0");
        manager.shutdown();
    }
}