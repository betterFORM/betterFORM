package de.betterform.connector.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import junit.framework.TestCase;

public class URIUtilTest extends TestCase {
  
  private String uri0;
  private String uri0a;
  private String uri1;
  private String uri2;
  private String uri3;
  private String uri4;
  private String uri5;
  private String uri6;

  @Override
  protected void setUp() throws Exception {
    
    uri0 = "http://localhost:8080";
    uri0a = "http://localhost:8080/";
    uri1 = "http://localhost:8080/path1/path2/uri1.xml";
    uri2 = "http://localhost:8080/path1/path2/uri2.xml#coolFragmentString";
    uri3 = "http://localhost:8080/path1/path2/uri3.xml?q1=v1#coolFragmentString";
    uri4 = "http://localhost:8080/path1/path2/uri4.xml#coolFragmentString?q1=v1";
    uri5 = "http://localhost:8080/path1/path2/file1.xml?q1=v1&q2=v2&q4";
    uri6 = "http://localhost:8080/path1/path2?q1=v1&q2=v2&q4";
  }
  
  public void testGetURIWithoutFragment() throws URISyntaxException {
    
    assertEquals("http://localhost:8080/path1/path2/uri1.xml",       URIUtil.getURIWithoutFragment(uri1));
    assertEquals("http://localhost:8080/path1/path2/uri2.xml",       URIUtil.getURIWithoutFragment(uri2));
    assertEquals("http://localhost:8080/path1/path2/uri3.xml?q1=v1", URIUtil.getURIWithoutFragment(uri3));
    assertEquals("http://localhost:8080/path1/path2/uri4.xml?q1=v1", URIUtil.getURIWithoutFragment(uri4));
  }
  
  public void testHasParameter() throws URISyntaxException {
    
    URI uri = new URI(uri5);
    
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

    Map<String, String> p1 = URIUtil.getQueryParameters(new URI(uri0));
    Map<String, String> p2 = URIUtil.getQueryParameters(new URI(uri5));
    
    assertEquals(0, p1.size());
    
    assertEquals(3, p2.size());
    assertEquals("v1", p2.values().toArray()[0]);
    assertEquals("v2", p2.values().toArray()[1]);
    assertEquals(null, p2.values().toArray()[2]);
  }

  public void testGetLastSegmentFromPath() throws URISyntaxException {
    assertNull(URIUtil.getLastSegmentFromPath(null));
    assertNull(URIUtil.getLastSegmentFromPath(new URI(uri0)));
    assertNull(URIUtil.getLastSegmentFromPath(new URI(uri0a)));
    assertEquals("uri1.xml", URIUtil.getLastSegmentFromPath(new URI(uri1)));
    assertEquals("file1.xml", URIUtil.getLastSegmentFromPath(new URI(uri5)));
    assertEquals("path2", URIUtil.getLastSegmentFromPath(new URI(uri6)));
  }
  
  public void testGetPathWithoutLastSegment() throws URISyntaxException {
    assertNull(URIUtil.getPathWithoutLastSegment(null));
    assertEquals("", URIUtil.getPathWithoutLastSegment(new URI(uri0)));
    assertEquals("", URIUtil.getPathWithoutLastSegment(new URI(uri0a)));
    assertEquals("/path1/path2", URIUtil.getPathWithoutLastSegment(new URI(uri1)));
    assertEquals("/path1/path2", URIUtil.getPathWithoutLastSegment(new URI(uri5)));
    assertEquals("/path1", URIUtil.getPathWithoutLastSegment(new URI(uri6)));
  }
  
  public void testHashFileExtension() throws URISyntaxException {
    
    assertFalse(URIUtil.hasFileExtension(null, "xml"));
    assertFalse(URIUtil.hasFileExtension(uri0, null, null));
    assertFalse(URIUtil.hasFileExtension(null, null, null));
    
    assertFalse(URIUtil.hasFileExtension(uri0, "xml"));
    assertFalse(URIUtil.hasFileExtension(uri0a, "xml"));
    assertFalse(URIUtil.hasFileExtension(uri1, "xql"));
    assertTrue(URIUtil.hasFileExtension(uri1, new String[] {"xml", "xql"}));
  }
}
