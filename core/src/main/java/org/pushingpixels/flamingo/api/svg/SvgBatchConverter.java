package org.pushingpixels.flamingo.api.svg;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.CountDownLatch;

public class SvgBatchConverter {
    /**
     * Main method for testing.
     *
     * @param args First parameter should point to a folder with SVG images, and
     *             the second parameter should be the package name for the
     *             transcoded classes.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("param 0 : dir, param 1 : pkg");
            System.exit(1);
        }

        File dir = new File(args[0]);
        if (!dir.exists()) {
            return;
        }
        
        NamingStrategy namingStrategy = new DefaultNamingStrategy();
        
        for (File file : dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".svg") || name.endsWith(".svgz");
            }
        })) {
            String svgClassName = namingStrategy.getClassName(file);
            String javaClassFilename = dir + File.separator + svgClassName + ".java";
            
            System.err.println("Processing " + file.getName());

            try {
                final CountDownLatch latch = new CountDownLatch(1);
                final PrintWriter pw = new PrintWriter(javaClassFilename);

                SvgTranscoder transcoder = new SvgTranscoder(file.toURI().toURL(), svgClassName);
                transcoder.setJavaToImplementResizableIconInterface(false);
                transcoder.setJavaPackageName(args[1]);
                transcoder.setListener(new TranscoderListener() {
                    public Writer getWriter() {
                        return pw;
                    }

                    public void finished() {
                        latch.countDown();
                    }
                });
                transcoder.transcode();
                latch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
