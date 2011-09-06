/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.filter;

import de.betterform.agent.web.WebUtil;
import junit.framework.TestCase;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class BufferedHttpServletResponseWrapperTest extends TestCase {
	DummyHttpResponse dummyResponse = new DummyHttpResponse();
	BufferedHttpServletResponseWrapper wrapper = new BufferedHttpServletResponseWrapper(dummyResponse);
	
	
    public void test2WrappersShouldContainAllContents() throws IOException {
    	PrintWriter pw = wrapper.getWriter();
        pw.write("foo"); 
        pw.flush(); 
        pw.write("bar");
        
        pw = wrapper.getWriter();
        pw.flush();
        pw.close();
        

        assertEquals("foobar", wrapper.getDataAsString());//this fails

    }

    public void testGetWriter() throws Exception {
    	try{
    		DummyHttpResponse dummyResponse2 = new DummyHttpResponse();
    		BufferedHttpServletResponseWrapper wrapper2 = new BufferedHttpServletResponseWrapper(dummyResponse2);
    		ServletOutputStream out = wrapper2.getOutputStream();
    		assertNotNull(out);
    		PrintWriter pw = wrapper2.getWriter();
    		fail("getWriter called after getOutputStream");

    	} catch (Exception e) {
    		assertTrue("wrong exception", e instanceof IllegalStateException);
    		assertNotNull(e.getMessage());
    	}
    }

    public void testGetOutputStream() throws Exception {
    	try{
    		DummyHttpResponse dummyResponse2 = new DummyHttpResponse();
    		BufferedHttpServletResponseWrapper wrapper2 = new BufferedHttpServletResponseWrapper(dummyResponse2);
    		PrintWriter pw = wrapper2.getWriter();
    		assertNotNull(pw);
    		ServletOutputStream out = wrapper2.getOutputStream();
    		fail("getOutputStream called after getWriter");

    	} catch (Exception e) {
    		assertTrue("wrong exception", e instanceof IllegalStateException);
    		assertNotNull(e.getMessage());
    	}
    }

    public void testGetCharacterEncoding(){

    	String charEnc = wrapper.getCharacterEncoding();
    	assertEquals("ISO-8859-1", charEnc);
    }

    public void testGetMediaType(){
    	DummyHttpResponse dummyResponse2 = new DummyHttpResponse();
		BufferedHttpServletResponseWrapper wrapper2 = new BufferedHttpServletResponseWrapper(dummyResponse2);

    	String media = wrapper2.getMediaType();
    	assertEquals("", media);

    	wrapper2.setContentType("wrongType");
    	media = wrapper2.getMediaType();
    	assertEquals("", media);

    	wrapper2.setContentType(WebUtil.HTML_CONTENT_TYPE);
    	media = wrapper2.getMediaType();
    	assertEquals("text/html", media);
    }

    public void testHasXMLContentType(){
    	DummyHttpResponse dummyResponse2 = new DummyHttpResponse();
		BufferedHttpServletResponseWrapper wrapper2 = new BufferedHttpServletResponseWrapper(dummyResponse2);

		boolean isXML = wrapper2.hasXMLContentType();
		assertEquals(false, isXML);

		wrapper2.setContentType(WebUtil.HTML_CONTENT_TYPE);
		isXML = wrapper2.hasXMLContentType();
		assertEquals(false, isXML);

		wrapper2.setContentType(WebUtil.HTML_CONTENT_TYPE);
		isXML = wrapper2.hasXMLContentType();
		assertEquals(true, isXML);
    }

	class DummyHttpResponse implements HttpServletResponse {

		private String contentType;

		public void addCookie(Cookie arg0) {
			// TODO Auto-generated method stub
			
		}

		public void addDateHeader(String arg0, long arg1) {
			// TODO Auto-generated method stub
			
		}

		public void addHeader(String arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}

		public void addIntHeader(String arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		public boolean containsHeader(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		public String encodeRedirectURL(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String encodeRedirectUrl(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String encodeURL(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String encodeUrl(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public void sendError(int arg0) throws IOException {
			// TODO Auto-generated method stub
			
		}

		public void sendError(int arg0, String arg1) throws IOException {
			// TODO Auto-generated method stub
			
		}

		public void sendRedirect(String arg0) throws IOException {
			// TODO Auto-generated method stub
			
		}

		public void setDateHeader(String arg0, long arg1) {
			// TODO Auto-generated method stub
			
		}

		public void setHeader(String arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}

		public void setIntHeader(String arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		public void setStatus(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setStatus(int arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}

		public void flushBuffer() throws IOException {
			// TODO Auto-generated method stub
			
		}

		public int getBufferSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getCharacterEncoding() {
			// TODO Auto-generated method stub
			//return null;
			return "ISO-8859-1";
		}

        public String getContentType() {
            //return null;  //To change body of implemented methods use File | Settings | File Templates.
        	return this.contentType;
        }

        public Locale getLocale() {
			// TODO Auto-generated method stub
			return null;
		}

		public ServletOutputStream getOutputStream() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		public PrintWriter getWriter() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

        public void setCharacterEncoding(String s) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isCommitted() {
			// TODO Auto-generated method stub
			return false;
		}

		public void reset() {
			// TODO Auto-generated method stub
			
		}

		public void resetBuffer() {
			// TODO Auto-generated method stub
			
		}

		public void setBufferSize(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setContentLength(int arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setContentType(String arg0) {
			// TODO Auto-generated method stub
			this.contentType = arg0;
		}

		public void setLocale(Locale arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
