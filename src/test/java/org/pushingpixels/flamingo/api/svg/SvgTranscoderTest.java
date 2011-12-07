package org.pushingpixels.flamingo.api.svg;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import junit.framework.TestCase;
import org.apache.tools.ant.types.resources.Files;

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
        assertCompile(transcoded);
        
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
        assertCompile(transcoded);
    }

    public void testTranscodeLarge() throws Exception {
        File svg = new File("target/test-classes/svg/apache-feather.svg");
        File transcoded = new File(svg.getParentFile(), "apache_feather.java");
        final PrintWriter out = new PrintWriter(transcoded);

        SvgTranscoder transcoder = new SvgTranscoder(svg.toURI().toURL(), "apache_feather");
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
        assertCompile(transcoded);
    }
    
    private void assertCompile(File file) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        Iterable<? extends JavaFileObject> unit = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, unit);
        assertTrue("Compilation failed", task.call());

        fileManager.close();
    }
}
