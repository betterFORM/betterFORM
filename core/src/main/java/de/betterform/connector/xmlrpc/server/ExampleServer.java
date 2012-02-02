/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.xmlrpc.server;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ExampleServer {
    private WebServer server;

    public static void main(String[] args) {
        try {
            ExampleServer rpc = new ExampleServer();
            rpc.setServer(InetAddress.getByName("127.0.0.1"), 8088);
            rpc.addHandler("xmlrpc.server.properties");
            rpc.listen();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: localhost");
        }
    }

    public void setServer(InetAddress address, int port) {
        server = new WebServer(port, address);
        XmlRpcServerConfigImpl config = new XmlRpcServerConfigImpl();
        XmlRpcServer xmlRpcServer = server.getXmlRpcServer();
        xmlRpcServer.setConfig(config);
    }

    public void listen() {
    	try {
        server.start();
    	} catch (IOException e){
    		System.err.println("ExampleServer could not be started");
    }
    }

    public void addHandler(String props) {
    	try {
	    	PropertyHandlerMapping phm = new PropertyHandlerMapping();
	    	phm.load(Thread.currentThread().getContextClassLoader(), "de/betterform/connector/xmlrpc/server/xmlrpc.server.properties");
	    	XmlRpcServer xmlRpcServer = server.getXmlRpcServer();
	    	xmlRpcServer.setHandlerMapping(phm);
    	} catch (IOException e){
    		System.err.println("IOException while reading handler definitions from property file");
    		e.printStackTrace();
    }
    	catch (XmlRpcException e){
    		System.err.println("XMLRpcException while reading handler definitions from property file");
}
    }
}
