package de.betterform.agent.web.resources;

import com.yahoo.platform.yui.compressor.CssCompressor;

import java.io.*;
import java.util.ArrayList;

public class CSSResourceHandler {


    public static void main(String ... args) throws Exception {

        String bfCssDirectory = "/Users/dev/projects/betterform-git/src/main/resources/styles/";
        String dojoReleaseDir = "/Users/dev/projects/betterform-git/web/target/betterform/resources/scripts/release/";

        ArrayList<File> cssFiles = new ArrayList<File>();
        cssFiles.add(new File(bfCssDirectory + "xforms.css"));
        cssFiles.add(new File(bfCssDirectory + "betterform.css"));
        cssFiles.add(new File(dojoReleaseDir + "dojo/dojo/resources/dojo.css"));
        cssFiles.add(new File(dojoReleaseDir + "dojo/dojox/widget/Toaster/Toaster.css"));
        cssFiles.add(new File(dojoReleaseDir + "dojo/dijit/themes/soria/soria.css"));
        cssFiles.add(new File(dojoReleaseDir + "dojo/dojox/layout/resources/ResizeHandle.css"));

/*
        @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>release/dojo/dijit/themes/soria/soria.css";
            </xsl:when>
            <xsl:when test="contains(//body/@class, 'nihilo')">
        @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>release/dojo/dijit/themes/nihilo/nihilo.css";
            </xsl:when>
            <xsl:when test="contains(//body/@class, 'claro')">
        @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>release/dojo/dijit/themes/claro/claro.css";
            </xsl:when>
            <xsl:otherwise>
        @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>release/dojo/dijit/themes/tundra/tundra.css";
            </xsl:otherwise>
        </xsl:choose>
        @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>release/dojo/dojo/resources/dojo.css";
        @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>release/dojo/dojox/widget/Toaster/Toaster.css";
        @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>release/dojo/dojox/layout/resources/FloatingPane.css";
        @import "<xsl:value-of select="concat($contextroot,$scriptPath)"";
*/

        CSSResourceHandler cssOptimizer = new CSSResourceHandler();

        String optimizedCSS = cssOptimizer.optimizeCSSFiles(cssFiles);

        System.out.println("Result CSS: " + optimizedCSS);

    }



    /**
     * Calls optimizeCSS for all files of the given Array
     *
     * @param cssFiles Array of CSS files to optimize
     * @return optimized CSS as one single line String
     * @throws Exception
     */
    public String optimizeCSSFiles(ArrayList<File> cssFiles) throws Exception {
        StringBuilder optimizedCSS = new StringBuilder();
        for(File cssFile : cssFiles) {
            StringBuilder cssToOptimize = new StringBuilder();
            try {
                // File result = this.optimizeCSS(cssFile);

                BufferedReader bufferedReader = new BufferedReader(new FileReader(cssFile.getAbsolutePath()));
                String readLine;
                while ((readLine = bufferedReader.readLine()) != null) {
                    cssToOptimize.append(readLine);
                }
                bufferedReader.close();

                optimizedCSS.append(this.parseCSS(cssToOptimize.toString()));
                // cssToOptimize.append("\n");
                // System.out.println("Result File placed at: " + result.getAbsolutePath());
            }

            catch (IOException e) {
                throw new Exception(e);
            }
        }
        return optimizedCSS.toString();

    }


    public String parseCSS(String inputCSS) {

        Reader in = null;
        Writer out = new StringWriter();
        String result = "";

        try {
            InputStream inputResource = new ByteArrayInputStream(inputCSS.getBytes("UTF-8"));
            in = new InputStreamReader(inputResource);

            CssCompressor compressor = new CssCompressor(in);
            // Close the input stream first, and then open the output stream,
            // in case the output file should override the input file.
            in.close();
            in = null;
            compressor.compress(out, -1);
            result = out.toString() ;
            System.out.println("Result: " + result);
            out.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }






























}
