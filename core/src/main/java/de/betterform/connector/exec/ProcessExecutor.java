/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.connector.exec;

import java.io.*;

/**
 * Process execution utility.
 * <p/>
 * See <a href="http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps_p.html">
 * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps_p.html</a> for
 * motivation and reference.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ProcessExecutor.java 161 2005-11-30 14:41:17Z uli $
 */
public class ProcessExecutor {

    /**
     * Executes the given os command.
     *
     * @param dir the working dir for the command.
     * @param command the os command to execute.
     * @param output the stream to write stdout to.
     * @param error the stream to write stderr to.
     * @return the process exit code.
     * @throws IOException if an i/o error occurred during os command
     * execution.
     * @throws InterruptedException if a multithreading error occurred after os
     * command execution.
     */
    public int execute(File dir, String command, OutputStream output, OutputStream error) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(getSystemCommand(command), null, dir);

        new StreamGobbler(process.getInputStream(), output).start();
        new StreamGobbler(process.getErrorStream(), error).start();

        return process.waitFor();
    }

    // private helper

    private String[] getSystemCommand(String command) {
        String osName = System.getProperty("os.name");

        if (osName.startsWith("Windows")) {
            // NOTE: this only applies to the NT family (WinNT, Win2000, WinXP)
            // for the DOS family (Win95, Win98, WinME) use "command.com" instead
            return new String[]{"cmd.exe", "/C", command};
        }

        return new String[]{"/bin/sh", "-c", command};
    }

    private class StreamGobbler extends Thread {
        private InputStream input;
        private OutputStream output;

        public StreamGobbler(InputStream input, OutputStream output) {
            this.input = input;
            this.output = output;
        }

        public void run() {
            try {
                String line = null;
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.input));
                PrintWriter writer = new PrintWriter(this.output);

                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                }
                writer.flush();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
