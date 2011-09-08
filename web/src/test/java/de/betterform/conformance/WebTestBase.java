/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.conformance;


import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;
import com.thoughtworks.selenium.Selenium;


/**
 * Base class for black box testing of betterForm Web.
 * Author: Lars Windauer
 * Date: Jan 27, 2008
 * Last Updated:
 */
public abstract class WebTestBase extends SeleneseTestCase {


    protected static final String browser = "@BROWSER@";
    protected static final String port = "@PORT@";
    protected static final String host = "@HOST@";
    protected static final String seleniumHost = "@SELENIUMHOST@";

    protected String formURL;
    protected Selenium selenium;
    protected String speed = "10";

    /**
     * 
     * @throws Exception
     */
    public void setUp() throws Exception {
        if (this.formURL == null) {
            fail("formURL must not be null");
            return;
        }

        selenium = new DefaultSelenium(seleniumHost, 4444, browser, "http://" + host + ":" + port);
        selenium.start();
        selenium.setSpeed(speed);
        //Is this needed?
        // System.out.println("Open: " + this.path2forms + this.formURL);
        // selenium.open(this.path2forms + this.formURL);

    }

    public void tearDown() throws Exception {
        printFileName();
        selenium.stop();
        selenium = null;
    }

    /**
     * 
     * @param controlId
     * @param expectedValue
     * @param timeoutinSeconds
     * @throws InterruptedException
     */
    protected void waitForVisibleText(String controlId, String expectedValue, int timeoutinSeconds) throws InterruptedException {
        for (int second = 0; ; second++) {
            if (second >= timeoutinSeconds)
                fail("waiting for text ('" + controlId + "') timed out after " + timeoutinSeconds + ", expected value was '" + expectedValue + "'");
            try {
                if (expectedValue.equals(selenium.getText(controlId))) {
                    if (selenium.isVisible(controlId)) {
                        break;
                    }
                }
            } catch (Exception e) {                
            }
            Thread.sleep(1000);
        }

    }

    /**
     * 
     * 
     * @param controlId
     * @param expectedValue
     * @param timeoutinSeconds
     * @throws InterruptedException
     */
    protected void waitForValue(String controlId, String expectedValue, int timeoutinSeconds) throws InterruptedException {
        for (int second = 0; ; second++) {
            if (second >= timeoutinSeconds)
                fail("waiting for value ('" + controlId + "') timed out after " + timeoutinSeconds + ", expected value was '" + expectedValue + "'");
            try {
                if (expectedValue.equals(selenium.getValue(controlId)))
                    break;
            } catch (Exception e) {            
            }
            Thread.sleep(1000);
        }

    }
    protected void verifyXFormsControlValue(String id, String cssclass, String type, String readonly, String incremental, String title) throws InterruptedException {
            
            this.waitForValue("//*[@id='" + id + "']/@class", cssclass, 60);
            this.waitForValue("//*[@id='" + id + "']/@type", type, 60);
            this.waitForValue("//*[@id='" + id + "']/@xfreadonly", readonly, 60);
            this.waitForValue("//*[@id='" + id + "']/@xfincremental", incremental, 60);
            this.waitForValue("//*[@id='" + id + "']/@title", title, 60);
    }

    protected void xformstestEcho() throws InterruptedException {
        for (int second = 0; ; second++) {
            if (second >= 60) fail("timeout");
            try {
                if ("Results from echo.sh".equals(selenium.getTitle())) break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }
    }

    protected void printFileName() {
        final String firefox = "firefox";
        final String safari = "safari";
        final String msie = "msie";

        String userAgent = selenium.getEval("navigator.userAgent").toLowerCase();
        String fileName;

        //Detect OS
        if (userAgent.toLowerCase().contains("linux")) {
            fileName = "Linux_";
        } else if (userAgent.toLowerCase().contains("mac")) {
            fileName = "MacOS_";
        } else if (userAgent.toLowerCase().contains("win")) {
            fileName = "Windows_";
        } else {
            fileName = "Unknown_";
        }

        //Detect Browser and Version
        if (userAgent.toLowerCase().contains(firefox)) {
            int index = userAgent.indexOf(firefox)+ firefox.length() + 1;
            fileName = fileName + "Firefox_"+ userAgent.substring(index);
        } else if (userAgent.toLowerCase().contains(safari)) {
            int index = userAgent.indexOf(safari) + safari.length();
            fileName = fileName + "Safari_"+ userAgent.substring(index + 1, index +2);
        } else if (userAgent.toLowerCase().contains(msie)) {
            int index = userAgent.indexOf(msie) + msie.length();
            fileName = fileName + "MSIE_"+ userAgent.substring(index +1, index +3);            
        } else {
            fileName = fileName + "Unknown";
        }

        System.out.print(fileName);
    }

}
