/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */



package de.betterform.agent.web.filter;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
* @author Matthijs Wensveen <m.wensveen@func.nl>
*/
public class BufferedServletOutputStream extends ServletOutputStream
{
      private PrintStream stream;

      /** FilterServletOutputStream
       *
       * @param output
       */
      public BufferedServletOutputStream(OutputStream output) throws UnsupportedEncodingException
      {
        // reference implementation of ServletOutputStream in Apache Tomcat uses ISO-8859-1 as default char encoding.
        stream = new PrintStream(output,false,"ISO-8859-1");
      }

    /** FilterServletOutputStream
       *
       * @param output
       */
      public BufferedServletOutputStream(OutputStream output, String encoding) throws UnsupportedEncodingException
      {
        stream = new PrintStream(output,false,encoding);
      }

      /**
     * @param b
     * @see java.io.PrintStream#print(boolean)
     */
    public void print(boolean b) {
        stream.print(b);
    }

    /**
     * @param c
     * @see java.io.PrintStream#print(char)
     */
    public void print(char c) {
        stream.print(c);
    }

    /**
     * @param d
     * @see java.io.PrintStream#print(double)
     */
    public void print(double d) {
        stream.print(d);
    }

    /**
     * @param f
     * @see java.io.PrintStream#print(float)
     */
    public void print(float f) {
        stream.print(f);
    }

    /**
     * @param i
     * @see java.io.PrintStream#print(int)
     */
    public void print(int i) {
        stream.print(i);
    }

    /**
     * @param l
     * @see java.io.PrintStream#print(long)
     */
    public void print(long l) {
        stream.print(l);
    }

    /**
     * @param s
     * @see java.io.PrintStream#print(java.lang.String)
     */
    public void print(String s) {
        stream.print(s);
    }

    /**
     *
     * @see java.io.PrintStream#println()
     */
    public void println() {
        stream.println();
    }

    /**
     * @param x
     * @see java.io.PrintStream#println(boolean)
     */
    public void println(boolean x) {
        stream.println(x);
    }

    /**
     * @param x
     * @see java.io.PrintStream#println(char)
     */
    public void println(char x) {
        stream.println(x);
    }

    /**
     * @param x
     * @see java.io.PrintStream#println(double)
     */
    public void println(double x) {
        stream.println(x);
    }

    /**
     * @param x
     * @see java.io.PrintStream#println(float)
     */
    public void println(float x) {
        stream.println(x);
    }

    /**
     * @param x
     * @see java.io.PrintStream#println(int)
     */
    public void println(int x) {
        stream.println(x);
    }

    /**
     * @param x
     * @see java.io.PrintStream#println(long)
     */
    public void println(long x) {
        stream.println(x);
    }

    /**
     * @param x
     * @see java.io.PrintStream#println(java.lang.String)
     */
    public void println(String x) {
        stream.println(x);
    }

    /**
     * @param buf
     * @param off
     * @param len
     * @see java.io.PrintStream#write(byte[], int, int)
     */
    public void write(byte[] buf, int off, int len) {
        stream.write(buf, off, len);
    }

    /**
     * @param b
     * @throws IOException
     * @see java.io.FilterOutputStream#write(byte[])
     */
    public void write(byte[] b) throws IOException {
        stream.write(b);
    }

    /**
     * @param b
     * @see java.io.PrintStream#write(int)
     */
    public void write(int b) {
        stream.write(b);
    }

    @Override
    public void close() throws IOException {
        stream.close();
        super.close();
    }

    @Override
    public void flush() throws IOException {
        stream.flush();
        super.flush();
    } 
}
