package de.betterform.agent.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class WebFactoryTest {

    @Test
    public void testGetXsltURI() throws URISyntaxException {

        WebFactory wf = new WebFactory();

        URI uri = wf.getXsltURI("/some/path/", "file.xsl");

        assertThat(uri.getScheme(), is("file"));
        assertTrue(uri.toString().endsWith("/web/some/file.xsl"));

    }

}
