package org.pushingpixels.flamingo.api.svg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CheckerboardPainter;

public class SVGApplication {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        JFrame frame = new JFrame("Flamingo SVG Transcoder");
        SVGApplication app = new SVGApplication(frame);
        frame.getContentPane().add(app.createComponents());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocation(200, 200);
        frame.setVisible(true);
    }

    JFrame frame;

    JButton button = new JButton("Load...");

    JLabel label = new JLabel();

    JSVGCanvas svgCanvas = new JSVGCanvas();

    String lastDir;

    public SVGApplication(JFrame f) {
        frame = f;
    }

    public JComponent createComponents() {
        final JXPanel panel = new JXPanel(new BorderLayout());
        panel.setBackgroundPainter(new CheckerboardPainter(Color.WHITE, new Color(0xF0F0F0), 15));
        
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(button);
        p.add(label);
        
        svgCanvas.setBackground(new Color(0, 0, 0, 0));
        
        panel.add("North", p);
        panel.add("Center", svgCanvas);

        // Set the button action.
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fc = new JFileChooser(lastDir);
                int choice = fc.showOpenDialog(panel);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    lastDir = f.getParent();
                    try {
                        String svgClassName = new IconSuffixNamingStrategy(new CamelCaseNamingStrategy()).getClassName(f);

                        svgCanvas.setURI(f.toURI().toURL().toString());

                        String javaClassFilename = f.getParent() + File.separator + svgClassName + ".java";

                        final PrintWriter pw = new PrintWriter(javaClassFilename);

                        SvgTranscoder transcoder = new SvgTranscoder(f.toURI().toURL(), svgClassName);
                        transcoder.setJavaToImplementResizableIconInterface(true);
                        transcoder.setListener(new TranscoderListener() {
                            public Writer getWriter() {
                                return pw;
                            }

                            public void finished() {
                                JOptionPane.showMessageDialog(null, "Finished");
                            }
                        });
                        transcoder.transcode();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Set the JSVGCanvas listeners.
        svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
            @Override
            public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
                label.setText("Document Loading...");
            }

            @Override
            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
                label.setText("Document Loaded.");
            }
        });

        svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            @Override
            public void gvtBuildStarted(GVTTreeBuilderEvent e) {
                label.setText("Build Started...");
            }

            @Override
            public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
                label.setText("Build Done.");
                frame.pack();
            }
        });

        svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
            @Override
            public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
                label.setText("Rendering Started...");
            }

            @Override
            public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
                label.setText("");
            }
        });

        return panel;
    }

}
