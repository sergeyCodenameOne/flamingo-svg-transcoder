package org.pushingpixels.flamingo.api.svg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CheckerboardPainter;
import org.jdesktop.swingx.renderer.DefaultListRenderer;

public class SVGApplication {

    private static final String APPNAME = "Flamingo SVG Transcoder";
    
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        JFrame frame = new JFrame(APPNAME);
        SVGApplication app = new SVGApplication(frame);
        frame.getContentPane().add(app.createComponents());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocation(200, 200);
        frame.setVisible(true);
        
        List<Image> images = new ArrayList<Image>();
        images.add(new ImageIcon(SVGApplication.class.getClassLoader().getResource("svg-16x16.png")).getImage());
        images.add(new ImageIcon(SVGApplication.class.getClassLoader().getResource("svg-32x32.png")).getImage());
        images.add(new ImageIcon(SVGApplication.class.getClassLoader().getResource("svg-48x48.png")).getImage());
        images.add(new ImageIcon(SVGApplication.class.getClassLoader().getResource("svg-64x64.png")).getImage());
        images.add(new ImageIcon(SVGApplication.class.getClassLoader().getResource("svg-128x128.png")).getImage());
        images.add(new ImageIcon(SVGApplication.class.getClassLoader().getResource("svg-256x256.png")).getImage());
        frame.setIconImages(images);
    }

    private JFrame frame;
    private JButton button = new JButton("Load...");
    private JLabel label = new JLabel();
    private JSVGCanvas svgCanvas = new JSVGCanvas();
    private String lastDir;
    private JComboBox comboTemplates = new JComboBox();

    public SVGApplication(JFrame frame) {
        this.frame = frame;
    }

    public JComponent createComponents() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createToolbar(), BorderLayout.NORTH);
        panel.add(createCanvas(), BorderLayout.CENTER);
        
        return panel;
    }

    public JComponent createToolbar() {
        JPanel toolbar = new JPanel(new MigLayout("ins 1r, fill"));

        try {
            Template[] templates = {
                new Template("icon.template"),
                new Template("plain.template"),
                new Template("resizable.template")
            };
            comboTemplates.setModel(new DefaultComboBoxModel(templates));
        } catch (IOException e) {
            e.printStackTrace();
        }
        comboTemplates.setRenderer(new DefaultListRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                String url = ((Template) value).getURL().toString();
                String label = null;
                if (url.contains("icon.template")) {
                    label = "Swing Icon";
                } else if (url.contains("plain.template")) {
                    label = "Plain Java2D";
                } else if (url.contains("resizable.template")) {
                    label = "Flamingo Resizable Icon";
                } else {
                    label = url.substring(url.lastIndexOf("/") + 1);
                }
                
                return super.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
            }
        });

        toolbar.add(new JLabel("Template:"), "right");
        toolbar.add(comboTemplates);
        toolbar.add(label, "growx, push");
        toolbar.add(button, "width button");
        
        // Set the button action.
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JFileChooser fc = new JFileChooser(lastDir);
                fc.setMultiSelectionEnabled(false);
                fc.setFileFilter(new FileNameExtensionFilter("SVG images", "svg", "svgz"));
                int choice = fc.showOpenDialog(button.getTopLevelAncestor());
                if (choice == JFileChooser.APPROVE_OPTION && fc.getSelectedFile().exists()) {
                    final File file = fc.getSelectedFile();
                    lastDir = file.getParent();
                    try {
                        svgCanvas.setURI(file.toURI().toURL().toString());
                        frame.setTitle(APPNAME + " - [" + file.getAbsolutePath() + "]");

                        new SwingWorker() {
                            @Override
                            protected Object doInBackground() throws Exception {
                                setMessage("Transcoding...");
                                
                                String svgClassName = new IconSuffixNamingStrategy(new CamelCaseNamingStrategy()).getClassName(file);
                                
                                String javaClassFilename = file.getParent() + File.separator + svgClassName + ".java";

                                PrintWriter out = new PrintWriter(javaClassFilename);
                                
                                SvgTranscoder transcoder = new SvgTranscoder(file.toURI().toURL(), svgClassName);
                                transcoder.setTemplate((Template) comboTemplates.getSelectedItem());
                                transcoder.setPrintWriter(out);
                                transcoder.transcode();
                                
                                return null;
                            }

                            @Override
                            protected void done() {
                                setMessage("Transcoding completed");
                            }
                        }.execute();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        return toolbar;
    }

    public JComponent createCanvas() {
        JXPanel panel = new JXPanel(new BorderLayout());
        panel.setBackgroundPainter(new CheckerboardPainter(Color.WHITE, new Color(0xF0F0F0), 15));
        panel.add(svgCanvas);
        svgCanvas.setBackground(new Color(0, 0, 0, 0));
        
        // Set the JSVGCanvas listeners.
        svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
            @Override
            public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
                setMessage("Document Loading...");
            }

            @Override
            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
                setMessage("Document Loaded.");
            }
        });

        svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            @Override
            public void gvtBuildStarted(GVTTreeBuilderEvent e) {
                setMessage("Build Started...");
            }

            @Override
            public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
                setMessage("Build Done.");
                frame.pack();
            }
        });

        svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
            @Override
            public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
                setMessage("Rendering Started...");
            }

            @Override
            public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
                setMessage("");
            }
        });

        return panel;
    }

    private void setMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                label.setText(message);
            }
        });
    }
}
