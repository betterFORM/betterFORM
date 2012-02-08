/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.conformance;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

/**
 * betterFORM Project
 * User: Tobi Krebs
 * Date: 12.12.11
 * Time: 14:13
 */
public abstract class WebDriverTest implements WebDriverTestInterface {
    private final static String HTMLUNIT = "HTMLUNIT";
    private final static String FIREFOX = "FIREFOX";
    private final static String CHROME = "CHROME";
    private final static String IE = "IE";
    //private final static String SAFARI = "SAFARI";

    protected WebDriver webDriver;
    
    @Before
    public void setUp() {
        String driver = System.getProperties().getProperty("test.driver", WebDriverTest.FIREFOX).toUpperCase();
        if (driver.equals(WebDriverTest.HTMLUNIT)) {
            this.webDriver= new HtmlUnitDriver();
        } else if (driver.equals(WebDriverTest.FIREFOX)) {
            this.webDriver = new FirefoxDriver();
        } else if (driver.equals(WebDriverTest.CHROME)) {
            /* Use "chrome.binary" to set path to binary */
            this.webDriver = new ChromeDriver();
        } else if (driver.equals(WebDriverTest.IE)) {
            if (System.getProperty("os.name").toLowerCase().indexOf("win") > 0) {
                this.webDriver = new InternetExplorerDriver();
            } else {
                fail("You specified InternetExplorer on a non Microsoft Operating System!");
            }
        }

        this.webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() throws Exception {
        this.webDriver.quit();
    }
}
