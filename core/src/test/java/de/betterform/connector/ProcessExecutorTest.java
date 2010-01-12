// Copyright 2010 betterForm
package de.betterform.connector;

import junit.framework.TestCase;
import de.betterform.connector.exec.ProcessExecutor;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Unit tests for the Process execution utility.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ProcessExecutorTest.java 161 2005-11-30 14:41:17Z uli $
 */
public class ProcessExecutorTest extends TestCase {
    private static String W32_BAT = "ProcessExecutorTest.bat";
    private static String UNIX_SH = "ProcessExecutorTest.sh";

    private File file;

    /**
     * Tests execute().
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testExecute() throws Exception {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        int exit = new ProcessExecutor().execute(this.file.getParentFile(), this.file.getAbsolutePath(), stdout, stderr);

        if(!System.getProperty("os.name").startsWith("Windows")){
            assertEquals(126, exit);
        }else{
            assertEquals(0, exit);
            assertEquals("stdout" + System.getProperty("line.separator"), stdout.toString());
            assertEquals("stderr" + System.getProperty("line.separator"), stderr.toString());
        }
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        String path = System.getProperty("os.name").startsWith("Windows")
                ? getClass().getResource(W32_BAT).getPath()
                : getClass().getResource(UNIX_SH).getPath();
        this.file = new File(path);
    }

    /**
     * @see junit.framework.TestCase#tearDown() 
     */
    protected void tearDown() throws Exception {
        this.file = null;
    }

}
