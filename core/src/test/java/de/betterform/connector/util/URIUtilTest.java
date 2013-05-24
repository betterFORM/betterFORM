package de.betterform.connector.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import junit.framework.TestCase;
import de.betterform.connector.exist.ExistUtils;

public class URIUtilTest  extends TestCase {
  
  public void testGetURIWithoutFragment() throws URISyntaxException {
    String uri1 = "http://localhost:8080/path1/path2/uri1.xml";
    String uri2 = "http://localhost:8080/path1/path2/uri2.xml#coolFragmentString";
    String uri3 = "http://localhost:8080/path1/path2/uri3.xml?q1=v1#coolFragmentString";
    String uri4 = "http://localhost:8080/path1/path2/uri4.xml#coolFragmentString?q1=v1";
    
    assertEquals("http://localhost:8080/path1/path2/uri1.xml",       URIUtils.getURIWithoutFragment(uri1));
    assertEquals("http://localhost:8080/path1/path2/uri2.xml",       URIUtils.getURIWithoutFragment(uri2));
    assertEquals("http://localhost:8080/path1/path2/uri3.xml?q1=v1", URIUtils.getURIWithoutFragment(uri3));
    assertEquals("http://localhost:8080/path1/path2/uri4.xml?q1=v1", URIUtils.getURIWithoutFragment(uri4));
  }
  
  public void testHasParameter() throws URISyntaxException {
    
    URI uri = new URI("http://localhost:8080/path1/path2/file1.xml?q1=v1&q2=v2&q4");
    
    assertFalse(URIUtils.hasParameter(null, "q1", "v1"));
    assertFalse(URIUtils.hasParameter(uri, "q3", "v1"));
    assertFalse(URIUtils.hasParameter(uri, "q1", "v2"));
    assertFalse(URIUtils.hasParameter(uri, null, "v2"));
    assertFalse(URIUtils.hasParameter(uri, "q1", null));
    assertFalse(URIUtils.hasParameter(uri, null, null));
    assertFalse(URIUtils.hasParameter(uri, "q4", "v1"));
    
    assertTrue(URIUtils.hasParameter(uri, "q1", "v1"));
    assertTrue(URIUtils.hasParameter(uri, "q2", "*"));
    assertTrue(URIUtils.hasParameter(uri, "q4", null));
  }
  
  public void testGetQueryParameters() throws URISyntaxException {
    
    Map<String, String> p1 = URIUtils.getQueryParameters(new URI("http://localhost:8080"));
    Map<String, String> p2 = URIUtils.getQueryParameters(new URI("http://localhost:8080/path1/path2/file1.xml?q1&q2=v2"));
    
    assertEquals(0, p1.size());
    
    assertEquals(2, p2.size());
    assertEquals(null, p2.values().toArray()[0]);
    assertEquals("v2", p2.values().toArray()[1]);
    
  }
  
  public void testCalculateArity() throws URISyntaxException {
    
    Map<String, String> p1 = URIUtils.getQueryParameters(new URI("http://localhost:8080"));
    Map<String, String> p2 = URIUtils.getQueryParameters(new URI("http://localhost:8080/file1.xqm?type=module&function=foo&k1=v1&k2=v2"));
    
    assertEquals(0, ExistUtils.calculateArity(p1));
    assertEquals(2, ExistUtils.calculateArity(p2));
  }

}
