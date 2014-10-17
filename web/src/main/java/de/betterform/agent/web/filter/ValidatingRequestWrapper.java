/*
 * Copyright (c) 2003-2008, KNOPFLERFISH project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 *
 * - Neither the name of the KNOPFLERFISH project nor the names of its
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.betterform.agent.web.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import java.net.URL;

public class ValidatingRequestWrapper extends HttpServletRequestWrapper {

    // private fields
    private HttpServletRequest request;

    // constructors
    public ValidatingRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    // implements ServletRequest

    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    public Enumeration getAttributeNames() {
        return request.getAttributeNames();
    }

    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    public int getContentLength() {
        return request.getContentLength();
    }

    public String getContentType() {
        return request.getContentType();
    }

    public ServletInputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

    public Locale getLocale() {
        return request.getLocale();
    }

    public Enumeration getLocales() {
        return request.getLocales();
    }

    public String getParameter(String name) {
        return request.getParameter(name);
    }

    public Enumeration getParameterNames() {
        return request.getParameterNames();
    }

    public String[] getParameterValues(String name) {
        return request.getParameterValues(name);
    }

    public String getProtocol() {
        return request.getProtocol();
    }

    public BufferedReader getReader() throws IOException {
        return request.getReader();
    }

    public String getRealPath(String path) {
        return request.getRealPath(path); // deprecated
    }

    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    public RequestDispatcher getRequestDispatcher(String uri) {
        return request.getRequestDispatcher(uri);
    }

    public String getScheme() {
        return request.getScheme();
    }

    public String getServerName() {
        return request.getServerName();
    }

    public int getServerPort() {
        return request.getServerPort();
    }

    public boolean isSecure() {
        return request.isSecure();
    }

    public void removeAttribute(String name) {
        request.removeAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        request.setAttribute(name, value);
    }

    // implements HttpServletRequest

    public String getAuthType() {
        return request.getAuthType();
    }

    public String getContextPath() {
        return request.getContextPath();
    }

    public Cookie[] getCookies() {
        return request.getCookies();
    }

    public long getDateHeader(String name) {
        return request.getDateHeader(name);
    }

    public String getHeader(String name) {
        return request.getHeader(name);
    }

    public Enumeration getHeaderNames() {
        return request.getHeaderNames();
    }

    public Enumeration getHeaders(String name) {
        return request.getHeaders(name);
    }

    public int getIntHeader(String name) {
        return request.getIntHeader(name);
    }

    public String getMethod() {
        return request.getMethod();
    }

    public String getPathInfo() {
        return request.getPathInfo();
    }

    public String getPathTranslated() {
        return request.getPathTranslated();
    }

    public String getQueryString() {
        return request.getQueryString();
    }

    public String getRemoteUser() {
        return request.getRemoteUser();
    }

    public String getRequestURI() {
        return request.getRequestURI();
    }

    public String getRequestedSessionId() {
        return request.getRequestedSessionId();
    }

    public String getServletPath() {
        return request.getServletPath();
    }

    public HttpSession getSession() {
        return request.getSession();
    }

    public HttpSession getSession(boolean create) {
        return request.getSession(create);
    }

    public Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    public boolean isRequestedSessionIdFromCookie() {
        return request.isRequestedSessionIdFromCookie();
    }

    public boolean isRequestedSessionIdFromURL() {
        return request.isRequestedSessionIdFromURL();
    }

    public boolean isRequestedSessionIdFromUrl() {
        return request.isRequestedSessionIdFromUrl(); // deprecated
    }

    public boolean isRequestedSessionIdValid() {
        return request.isRequestedSessionIdValid();
    }

    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    public StringBuffer getRequestURL() {
        return request.getRequestURL();
    }

    public int getLocalPort() {
        return request.getLocalPort();
    }

    public int getRemotePort() {
        return request.getRemotePort();
    }

    public void setCharacterEncoding(String enc)
            throws UnsupportedEncodingException
    {
        request.setCharacterEncoding(enc);
    }

    public String getLocalAddr() {
        return request.getLocalAddr();
    }

    public java.util.Map getParameterMap() {
        return request.getParameterMap();
    }

    public String getLocalName() {
        return request.getLocalName();
    }



} // RequestWrapper
