package de.betterform.connector.serializer;

import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.model.submission.Submission;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;

/**
 * Author(s): Fabian Otto, Tobi Krebs
 * Date: Apr 8, 2010
 */
public class FormDataSerializerTest extends SerializeTest {
    private int numberOfKeys = 3;

   /**
    * Test for the boundary and the correct appearence of the serialized data
    * */
   public void testSerialize() throws Exception {
        SerializerRequestWrapper wrapper = serialize("submission-form-data-post");
        String string                    = wrapper.getBodyStream().toString();
        String boundary                  = wrapper.getHeader("internal-boundary-mark");

        assertNotNull(boundary);
        assertTrue("Serialized data does not starts with \"--\"\n" + string, string.startsWith("\r\n--" + boundary));
        // remove starting \r\n and than split by boundary
        assertEquals(numberOfKeys + 1,  string.substring(2).split("--" + boundary + "\r\n").length);
    }

}
