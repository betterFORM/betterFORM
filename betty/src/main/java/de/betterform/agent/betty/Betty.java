/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.agent.betty;

import com.sun.java.browser.dom.DOMAccessException;
import com.sun.java.browser.dom.DOMUnsupportedException;
import de.betterform.connector.http.AbstractHTTPConnector;
import de.betterform.xml.config.Config;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xslt.TransformerService;
import de.betterform.xml.xslt.impl.CachingTransformerService;
import de.betterform.xml.xslt.impl.FileResourceResolver;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.applet.Applet;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Betty - the betterForm processor inside an Applet.
 * <p/>
 * todo: implement XFormsProcessor
 *
 * @author Joern Turner
 */
public class Betty extends Applet {
    private static final Log LOGGER = LogFactory.getLog(Betty.class);

    // applet parameters and defaults
    public static final String LOG4J_PARAMETER = "log4j";
    public static final String LOG4J_DEFAULT = "log4j.xml";
    public static final String CONFIG_PARAMETER = "config";
    public static final String CONFIG_DEFAULT = "betterform-config.xml";
    public static final String XSLT_PARAMETER = "xslt";
    public static final String XSLT_DEFAULT = "../resources/xslt/betterform-applet.xsl";

    // use by js to setup calendar control
    public static final String DATE_DISPLAY_FORMAT = "%d.%m.%Y";
    public static final String DATETIME_DISPLAY_FORMAT = "%d.%m.%Y %H:%M";

    // use by js for itemsets
    public static final String BETTERFORM_PSEUDO_ITEM = "betterform-pseudo-item";

    // splash / error screens
    private static final String SPLASH_SCREEN = "<div class='splash-screen'>@activity@ <b>@item@</b> ...</div>";
    private static final String ERROR_SCREEN = "<div class='error-screen'><div class='error-image'>&nbsp;</div><div>The following error occurred: <pre class='error-message'>@message@</pre></div></div>";
    private static final String ACTIVITY_TOKEN = "@activity@";
    private static final String ITEM_TOKEN = "@item@";
    private static final String MESSAGE_TOKEN = "@message@";

    private AppletProcessor appletProcessor;
    private String documentName;
    private String view;

    /**
     * Creates a new betterform applet.
     */
    public Betty() {
        // NOP

    }

    Image img;
    MediaTracker tr;

    public void paint(Graphics g) {
        tr = new MediaTracker(this);
        img = getImage(getCodeBase(), "../resources/images/betterform_icon16x16.png");
        tr.addImage(img, 0);
        g.drawImage(img, 0, 0, this);
    }


    /**
     * Returns the Applet info.
     *
     * @return the Applet info.
     */
    public String getAppletInfo() {
        // todo: own version number ?
        return "Betty/" + XFormsProcessorImpl.getAppInfo();
    }

    /**
     * Init: Initializes an AppletAdapter.
     */
    public void init() {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Applet init start");
            }

            // set splash screen

//            javascriptCall("setSplash", getSplashScreen(XFormsProcessorImpl.getAppInfo(), ""));

            // get code and document bases
            URL codeBaseUrl = getCodeBase();
            URL documentBaseUrl = getDocumentBase();
            LOGGER.debug("getDocumentBase: " + documentBaseUrl.toExternalForm());

            // extract document name
            String documentPath = documentBaseUrl.getPath();
            this.documentName = documentPath.substring(documentPath.lastIndexOf('/') + 1);

            // update splash screen
//            javascriptCall("setSplash", getSplashScreen("configuring", this.documentName));

            // init logging
            URI log4jUrl = resolveParameter(codeBaseUrl, LOG4J_PARAMETER, LOG4J_DEFAULT);
            DOMConfigurator.configure(log4jUrl.toURL());

            LOGGER.info("init: code base '" + codeBaseUrl + "'");
            LOGGER.info("init: document base '" + documentBaseUrl + "'");
            LOGGER.info("init: document name '" + this.documentName + "'");

            // loading config
            URI configUrl = resolveParameter(codeBaseUrl, CONFIG_PARAMETER, CONFIG_DEFAULT);
            LOGGER.info("configURL '" + configUrl + "'");
            if (configUrl.toString().startsWith("file:")) {
                LOGGER.info("loading from file:");
                Config.getInstance(new FileInputStream(new File(configUrl)));
            } else {
                LOGGER.info("loading from http:");
                Config.getInstance(configUrl.toURL().openConnection().getInputStream());
            }

            // init adapter
            this.appletProcessor = initAdapter(documentBaseUrl);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            handleException(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Start: Renders the initial form.
     */
    public void start() {
        System.out.println("Applet start");

        try {
            // update splash screen
//            javascriptCall("setSplash", getSplashScreen("rendering", this.documentName));

            // render initial view
            if (this.appletProcessor != null) {
                System.out.println("appletProcessor not null");
            }
            String form = renderForm((Document) this.appletProcessor.getXForms());
            this.view = form;

            // set initial view
            javascriptCall("setView", form);
//            javascriptCall("setSplash", getSplashScreen(this.documentName, " ready"));

//            JSObject.getWindow(this).eval("setView('" + form + "')");
//            JSObject win = JSObject.getWindow(this);
//            ((JSObject)win.getMember("document")).call("setView", new String[]{form});

        }
        catch (Exception e) {
            handleException(e);
            throw new RuntimeException(e);
        }
    }

    public void callJS(String functionName, String[] args) {
        JSObject win = JSObject.getWindow(this);
        ((JSObject) win.getMember("document")).call(functionName, args);
    }

    public String getView() {
        return this.view;
    }

    /**
     * Stop: Does nothing yet.
     */
    public void stop() {

        //System.out.println("stop: " + this);
    }

    /**
     * Destroy: Performs shutdown on the AppletAdapter and releases all
     * resources.
     */
    public void destroy() {
        try {

            //System.out.println("destroy: " + this);

            this.appletProcessor.shutdown();
            this.appletProcessor = null;
        }
        catch (Exception e) {
            handleException(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the URL of the document in which this applet is embedded.
     * <p/>
     * This method is overridden in order to get access to the URL query part.
     * Since Firefox passes URL query parts even for 'file:' URLs, MS IE does
     * not so.
     *
     * @return the URL of the document that contains this applet.
     */
    public URL getDocumentBase() {
        try {
            System.out.println("Get JSObject");
            JSObject applet = JSObject.getWindow(this);
            System.out.println("Applet Info: " + applet);
            String location = applet.eval("location.href").toString();
            System.out.println("Location: " + location);
            return Betty.toUNCAwareURL(location);
        }
        catch (MalformedURLException e) {
            return super.getDocumentBase();
        }
    }

    // api for javascript to update the processor

    /**
     * Called by Javascript to add a message to the debug log.
     *
     * @param debug the debug message.
     */
    public void debug(String debug) {

        //System.out.println("JS: " + debug);
        getAppletContext().showStatus(debug);
    }

    /**
     * Called by Javascript to dispatch an event into the internal DOM.
     *
     * @param id        the id of the target element.
     * @param eventType the type of event to be fired.
     */
    public void dispatch(String id, String eventType) {
        LOGGER.debug("Betty Applet dispatch - " + id + " : " + eventType);
        try {
            this.appletProcessor.dispatch(id, eventType);
            this.appletProcessor.executeHandler();
        }
        catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Called by Javascript to update a control's value.
     *
     * @param id    the id of the control.
     * @param value the new value of the control.
     */
    public void setValue(String id, String value) {
        LOGGER.debug("Betty Applet setValue: " + id + " : '" + value + "'");
        try {
            this.appletProcessor.setValue(id, value);
            this.appletProcessor.executeHandler();
        }
        catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Called by Javascript to update an upload's value.
     *
     * @param id          the id of the upload control.
     * @param value       the absolute name of the file to be uploaded.
     * @param type        the optional file mediatype.
     * @param destination the optional file destination.
     */
    public void setValue(String id, String value, String type, String destination) {
        try {
            this.appletProcessor.setValue(id, value, type, destination);
            this.appletProcessor.executeHandler();
        }
        catch (Exception e) {
            handleException(e);
        }
    }

    /**
     * Called by Javascript to update a repeat's index.
     *
     * @param id    the id of the repeat.
     * @param index the new repeat index.
     */
    public void setRepeatIndex(String id, String index) {
        try {
            this.appletProcessor.setRepeatIndex(id, Integer.parseInt(index));
        }
        catch (XFormsException e) {
            handleException(e);
        }
    }

    // javascript api

    /**
     * Evaluates the specified javascript.
     *
     * @param string the script to evaluate.
     */
    public void javascriptEval(String string) {
        JSObject.getWindow(this).eval(string);
    }

    public void handleStateChanged(String id,
                                   String value,
                                   String valid,
                                   String reasdonly,
                                   String required,
                                   String relevant,
                                   String type) {


        //could probably better a JSON string or associative array if possible
        if (value == null) value = "";
        if (valid == null) valid = "";
        if (reasdonly == null) reasdonly = "";
        if (required == null) required = "";
        if (relevant == null) relevant = "";
        if (type == null) type = "";

        Object args[];
        args = new Object[7];
        args[0] = id;
        args[1] = value;
        args[2] = valid;
        args[3] = reasdonly;
        args[4] = required;
        args[5] = relevant;
        args[6] = type;

//        JSObject.getWindow(this).call("handleStateChange", args);
    }

    /**
     * Calls the specified javascript method.
     *
     * @param name the method name.
     * @param arg  the method argument.
     */
    public void javascriptCall(String name, String arg) throws JSException {
        javascriptCall(name, new String[]{arg});
    }

    /**
     * Calls the specified javascript method.
     *
     * @param name the method name.
     * @param args the method arguments.
     */
    public void javascriptCall(String name, String[] args) throws JSException {
        JSObject.getWindow(this).call(name, args);
    }

    // static helper

    /**
     * Fixes creation of URLs from strings.
     * <p/>
     * There's a special treatment for <code>file:</code> URLs in order to keep UNC path names.
     *
     * @param url the URL in string representation.
     * @return the URL object.
     * @throws MalformedURLException if the string representation is malformed.
     */
    public static URL toUNCAwareURL(String url) throws MalformedURLException {
        if (url == null) {
            return null;
        }

        if (url.startsWith("file:")) {
            return new File(url.substring(5)).toURL();
        }

        return new URL(url);
    }

    public static URI toFileCompatibleURI(URL url) throws URISyntaxException {
        if (url == null) {
            return null;
        }

        String string = url.toString();
        if (url.getProtocol().equals("file") && url.getQuery() != null) {
            return new URI(string.substring(0, string.indexOf('?')));
        }

        return new URI(string);
    }

    /**
     * Parses URL parameters and stores them in the provided map.
     *
     * @param url the URL parameter string.
     */
    public static void parseParameters(URL url, Map map) {
        if (url != null && url.getQuery() != null && url.getQuery().length() > 0) {
            String[] list = url.getQuery().split("&");
            String[] tupel;

            for (int index = 0; index < list.length; index++) {
                tupel = list[index].split("=");
                map.put(tupel[0], tupel.length > 1 ? tupel[1] : "");


                //System.out.println("found parameter: " + tupel[0] + "=" + map.get(tupel[0]));
            }
        }
    }

    public static void parseCookies(Applet applet, Map map) {
        Cookie[] cookies = getCookies(applet);
        if (cookies != null && cookies.length != 0) {
            map.put(AbstractHTTPConnector.REQUEST_COOKIE, cookies);
        }
    }

    static Cookie[] getCookies(Applet applet) {
        JSObject win = JSObject.getWindow(applet);
        JSObject doc = (JSObject) win.getMember("document");
        String cookieString = (String) doc.getMember("cookie");

        if (cookieString != null && cookieString.length() != 0) {
            JSObject url = (JSObject) win.getMember("location");
            String host = (String) url.getMember("host");

            //loose path handling for now - above is sometimes maybe more appropriate
            //String path = (String) url.getMember("pathname");

            //parse cookiestring
            String[] browserCookies = cookieString.split(";");
            BasicClientCookie[] cookies = new BasicClientCookie[browserCookies.length];

            for (int i = 0; i < browserCookies.length; i++) {
                String cName = browserCookies[i].substring(0, browserCookies[i].indexOf("="));
                String cValue = browserCookies[i].substring(browserCookies[i].indexOf("=") + 1);

                cookies[i] = new BasicClientCookie(cName, cValue);
                cookies[i].setDomain(host);
                cookies[i].setPath("/");
                cookies[i].setSecure(false);

                //System.out.println("found cookie: " + cookies[i].toExternalForm());

            }
            return cookies;
        }

        return null;
    }

    // protected template methods

    protected AppletProcessor initAdapter(URL documentBaseUrl) throws MalformedURLException, XFormsException, URISyntaxException, DOMUnsupportedException, DOMAccessException, TransformerException {
        // update splash screen
//        javascriptCall("setView", getSplashScreen("initializing", this.documentName));

        // create context and uri
        Map context = new HashMap();
        URI documentBaseURI = Betty.toFileCompatibleURI(documentBaseUrl);

        // parse cookies ans parameters
        Betty.parseCookies(this, context);
        if (documentBaseUrl.getQuery() != null) {
            Betty.parseParameters(documentBaseUrl, context);
        }

        // setup processor
        AppletProcessor processor = new AppletProcessor();
        processor.setBetterFormApplet(this);
        processor.setContextClassLoader(Thread.currentThread().getContextClassLoader());
        processor.setUploadDir(new File(documentBaseURI).getParentFile().getAbsolutePath());
        //hack around AbstractBetterFormAdapter to set contextmap
        processor.setContext(context);
        processor.setBaseURI(documentBaseURI.toString());
        processor.setXForms(documentBaseURI);

        // init processor
        processor.init();
        return processor;
    }

    protected URI resolveParameter(URL base, String name, String value) throws URISyntaxException, MalformedURLException {
        String relative = getParameter(name);
        if (relative == null || relative.length() == 0) {
            relative = value;
        }

//        return new URI(base.toString()).resolve(relative).toURL();
        return new URI(base.toString()).resolve(relative);
    }

    protected String renderForm(Document document) throws Exception {
        // todo: determine from browser document
        String encoding = "UTF-8";

        LOGGER.debug("renderForm");
        // obtain transformer for stylesheet uri
        String stylesheetParameter = getParameter(XSLT_PARAMETER);
        if (stylesheetParameter == null || stylesheetParameter.length() == 0) {
            stylesheetParameter = XSLT_DEFAULT;
        }
        LOGGER.debug("stylesheetParameter: " + stylesheetParameter);
        URI stylesheetURI = new URI(getCodeBase().toString()).resolve(stylesheetParameter);
        LOGGER.debug("styleSheetURI: " + stylesheetURI);

        TransformerService transformerService = new CachingTransformerService(new FileResourceResolver());
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
        Transformer transformer = transformerService.getTransformer(stylesheetURI);


        // setup source and result objects
        Source documentSource = new DOMSource(document);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Result streamResult = new StreamResult(stream);

        // go
        transformer.setParameter("debug-enabled", String.valueOf(LOGGER.isDebugEnabled()));
        transformer.setParameter("betterform-pseudo-item", BETTERFORM_PSEUDO_ITEM);
        transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
        transformer.transform(documentSource, streamResult);

        String result = stream.toString(encoding);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("result >>>>>:" + result);
        }

//        return result.substring(result.indexOf("<form"),result.lastIndexOf("</html>"));
        return result.substring(result.indexOf("<form"));
    }

    protected String toString(Node node) throws TransformerException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DOMUtil.prettyPrintDOM(node, stream);
        return System.getProperty("line.separator") + stream;
    }

    protected String getSplashScreen(String activity, String item) {
        return replace(replace(SPLASH_SCREEN, ACTIVITY_TOKEN, activity), ITEM_TOKEN, item);
    }

    protected String getErrorScreen(String message) {
        return replace(ERROR_SCREEN, MESSAGE_TOKEN, message);
    }

    protected String replace(String string, String match, String replacement) {
        int start = string.indexOf(match);
        if (start > -1) {
            StringBuffer buffer = new StringBuffer(string);
            buffer.replace(start, start + match.length(), replacement);
            return buffer.toString();
        }

        return string;
    }

    protected void handleException(Exception exception) {
        OutputStream stream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(stream);
        exception.printStackTrace(writer);
        writer.flush();

//        javascriptCall("setView", getErrorScreen(stream.toString()));
    }

}

// end of class
