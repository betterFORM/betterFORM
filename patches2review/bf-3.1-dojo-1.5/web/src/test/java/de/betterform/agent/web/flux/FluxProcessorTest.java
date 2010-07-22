package de.betterform.agent.web.flux;

import junit.framework.TestCase;

import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.agent.web.session.SerializableObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class FluxProcessorTest extends TestCase {
/*
    static {        
        org.apache.log4j.BasicConfigurator.configure();
    }
*/

    private XFormsProcessor fluxProcessor;
    private static final Log LOGGER = LogFactory.getLog(FluxProcessorTest.class);



    @Override
    protected void setUp() throws Exception {
        this.fluxProcessor = new FluxProcessor();
        this.fluxProcessor.setXForms(getClass().getResourceAsStream("localization.xhtml"));
        //this.fluxProcessor.init();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }


    public void testEhcachSerialization() throws Exception {
        CacheManager manager = CacheManager.create();
        Ehcache sessionCache = manager.getCache("xfSessionCache");

        sessionCache.put(new Element("fluxProcessor",this.fluxProcessor));
        sessionCache.flush();

        sessionCache.put(new Element("2",new SerializableObject("foo")));
        sessionCache.flush();

        if(LOGGER.isDebugEnabled()) LOGGER.debug(sessionCache.getStatistics());

        FluxProcessor syncedProcessor = (FluxProcessor) sessionCache.get("fluxProcessor").getValue();
        assertNotNull(syncedProcessor);
        // syncedProcessor.init();

/*
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("START PRETTY PRINT");
            DOMUtil.prettyPrintDOM(syncedProcessor.getXForms());
            LOGGER.debug("\nEND PRETTY PRINT");
        }
*/
        SerializableObject value2 = (SerializableObject) sessionCache.get("2").getValue();
        if(LOGGER.isDebugEnabled()) LOGGER.debug(sessionCache.getStatistics());

        assertEquals("foo", value2.getValue());
    }


    public void testGetAllUpdates() throws Exception{
        this.fluxProcessor.setLocale("de");
/*
        DOMUtil.prettyPrintDOM(((FluxProcessor)this.fluxProcessor).getEventLog().getLog());

        ((FluxProcessor)this.fluxProcessor).getEventLog().flush();
*/

//        DOMUtil.prettyPrintDOM(this.fluxProcessor.getXForms());
        this.fluxProcessor.setLocale("en");
//        DOMUtil.prettyPrintDOM(((FluxProcessor)this.fluxProcessor).getEventLog().getLog());
    }

}