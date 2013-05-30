package de.betterform.connector.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import junit.framework.TestCase;

public class URIUtilTest extends TestCase {
  
  public void testGetURIWithoutFragment() throws URISyntaxException {
    String uri1 = "http://localhost:8080/path1/path2/uri1.xml";
    String uri2 = "http://localhost:8080/path1/path2/uri2.xml#coolFragmentString";
    String uri3 = "http://localhost:8080/path1/path2/uri3.xml?q1=v1#coolFragmentString";
    String uri4 = "http://localhost:8080/path1/path2/uri4.xml#coolFragmentString?q1=v1";
    
    assertEquals("http://localhost:8080/path1/path2/uri1.xml",       URIUtil.getURIWithoutFragment(uri1));
    assertEquals("http://localhost:8080/path1/path2/uri2.xml",       URIUtil.getURIWithoutFragment(uri2));
    assertEquals("http://localhost:8080/path1/path2/uri3.xml?q1=v1", URIUtil.getURIWithoutFragment(uri3));
    assertEquals("http://localhost:8080/path1/path2/uri4.xml?q1=v1", URIUtil.getURIWithoutFragment(uri4));
  }
  
  public void testHasParameter() throws URISyntaxException {
    
    URI uri = new URI("http://localhost:8080/path1/path2/file1.xml?q1=v1&q2=v2&q4");
    
    assertFalse(URIUtil.hasParameter(null, "q1", "v1"));
    assertFalse(URIUtil.hasParameter(uri, "q3", "v1"));
    assertFalse(URIUtil.hasParameter(uri, "q1", "v2"));
    assertFalse(URIUtil.hasParameter(uri, null, "v2"));
    assertFalse(URIUtil.hasParameter(uri, "q1", null));
    assertFalse(URIUtil.hasParameter(uri, null, null));
    assertFalse(URIUtil.hasParameter(uri, "q4", "v1"));
    
    assertTrue(URIUtil.hasParameter(uri, "q1", "v1"));
    assertTrue(URIUtil.hasParameter(uri, "q2", "*"));
    assertTrue(URIUtil.hasParameter(uri, "q4", null));
  }
  
  public void testGetQueryParameters() throws URISyntaxException {
    
    Map<String, String> p1 = URIUtil.getQueryParameters(new URI("http://localhost:8080"));
    Map<String, String> p2 = URIUtil.getQueryParameters(new URI("http://localhost:8080/path1/path2/file1.xml?q1&q2=v2"));
    
    assertEquals(0, p1.size());
    
    assertEquals(2, p2.size());
    assertEquals(null, p2.values().toArray()[0]);
    assertEquals("v2", p2.values().toArray()[1]);
    
  }
}
