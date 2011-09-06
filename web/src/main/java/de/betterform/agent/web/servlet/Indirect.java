/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: fabian.otto@betterform.de
 * Date: Oct 2, 2009
 * Time: 2:40:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class Indirect extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int length               = request.getContentLength();
        ServletOutputStream out = response.getOutputStream();
        ServletInputStream input = request.getInputStream();
        int value;
        System.out.println("blub: " + request.getRemoteHost());
        out.print("OUTPUT");
        while( (value = input.read()) != -1){
            out.write(value);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletOutputStream out = response.getOutputStream();
        out.println("<h1>HTML Seite</h1>" +
                "<form method=\"post\"  >" +
                "<input type='submit'/>" +
                "</form>");

        return;
    }
}
