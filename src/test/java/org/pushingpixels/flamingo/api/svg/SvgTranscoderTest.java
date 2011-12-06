package org.pushingpixels.flamingo.api.svg;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Writer;

import junit.framework.TestCase;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SvgTranscoderTest extends TestCase {
    
    public void testTranscode() throws Exception {
        File svg = new File("target/test-classes/svg/edit-copy.svg");
        File transcoded = new File(svg.getParentFile(), "edit_copy.java");
        final PrintWriter out = new PrintWriter(transcoded);

        SvgTranscoder transcoder = new SvgTranscoder(svg.toURI().toURL(), "edit_copy");
        transcoder.setJavaPackageName("test.svg.transcoded");
        transcoder.setJavaToImplementResizableIconInterface(true);
        transcoder.setListener(new TranscoderListener() {
            public Writer getWriter() {
                return out;
            }

            public void finished() { }
        });

        transcoder.transcode();
        
        out.flush();
        out.close();
        
        assertTrue(transcoded.exists());
        
        assertEquals(new File("src/test/java/transcoded/edit_copy.java"), transcoded);
    }
    
    private void assertEquals(File file1, File file2) throws IOException {
        LineNumberReader in1 = new LineNumberReader(new FileReader(file1));
        LineNumberReader in2 = new LineNumberReader(new FileReader(file2));
        
        String line1 = null;
        String line2 = null;
        
        while ((line1 = in1.readLine()) != null | (line2 = in2.readLine()) != null) {
            assertEquals("Line " + in1.getLineNumber(), line1, line2);
        }
    }

    public void testTranscodeEmpty() throws Exception {
        File svg = new File("target/test-classes/svg/empty.svg");
        File transcoded = new File(svg.getParentFile(), "empty.java");
        final PrintWriter out = new PrintWriter(transcoded);

        SvgTranscoder transcoder = new SvgTranscoder(svg.toURI().toURL(), "empty");
        transcoder.setJavaPackageName("test.svg.transcoded");
        transcoder.setJavaToImplementResizableIconInterface(true);
        transcoder.setListener(new TranscoderListener() {
            public Writer getWriter() {
                return out;
            }

            public void finished() { }
        });

        transcoder.transcode();
        
        out.flush();
        out.close();
        
        assertTrue(transcoded.exists());
    }
}
