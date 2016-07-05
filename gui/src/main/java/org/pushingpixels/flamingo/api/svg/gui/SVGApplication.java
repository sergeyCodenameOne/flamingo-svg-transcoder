/**
 * Copyright 2014 Emmanuel Bourg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pushingpixels.flamingo.api.svg.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CheckerboardPainter;

import org.pushingpixels.flamingo.api.svg.CamelCaseNamingStrategy;
import org.pushingpixels.flamingo.api.svg.DefaultNamingStrategy;
import org.pushingpixels.flamingo.api.svg.IconSuffixNamingStrategy;
import org.pushingpixels.flamingo.api.svg.NamingStrategy;
import org.pushingpixels.flamingo.api.svg.SvgTranscoder;
import org.pushingpixels.flamingo.api.svg.Template;

public class SVGApplication {

    private static final String APPNAME = "Flamingo SVG Transcoder";
    
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        JFrame frame = new JFrame(APPNAME);
        SVGApplication app = new SVGApplication(frame);
        frame.getContentPane().add(app.createComponents());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((int) (screen.getWidth() - frame.getWidth()) / 2, (int) (screen.getHeight() - frame.getHeight()) / 2);
        frame.setVisible(true);
        frame.setIconImages(getApplicationIcons());
    }

    private static List<Image> getApplicationIcons() {
        List<Image> images = new ArrayList<Image>();
        images.add(new ImageIcon(SVGApplication.class.getClassLoader().getResource("svg-16x16.png")).getImage());
        images.add(new ImageIcon(SVGApplication.class.getClassLoader().getResource("svg-32x32.png")).getImage());
        images.add(new ImageIcon(SVGApplication.class.getClassLoader().getResource("svg-48x48.png")).getImage());
        images.add(new ImageIcon(SVGApplication.class.getClassLoader().getResource("svg-64x64.png")).getImage());
        images.add(new ImageIcon(SVGApplication.class.getClassLoader().getResource("svg-128x128.png")).getImage());
        images.add(new ImageIcon(SVGApplication.class.getClassLoader().getResource("svg-256x256.png")).getImage());
        return images;
    }

    private JFrame frame;
    private JButton button = new JButton("Load...");
    private JLabel label = new JLabel();
    private JSVGCanvas svgCanvas = new JSVGCanvas();
    private String lastDir;
    private JComboBox<Template> comboTemplates = new JComboBox<>();
    private JComboBox<NamingStrategy> comboNaming = new JComboBox<>();

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
        JPanel toolbar = new JPanel(new MigLayout("ins 1r, fill", "[][]2u[][]2u[]"));

        try {
            Template[] templates = {
                new Template("icon.template"),
                new Template("plain.template"),
                new Template("resizable.template")
            };
            comboTemplates.setModel(new DefaultComboBoxModel<>(templates));
        } catch (IOException e) {
            e.printStackTrace();
        }
        comboTemplates.setRenderer(new TemplateListCellRenderer());
        
        comboNaming.setModel(new DefaultComboBoxModel<>(new NamingStrategy[] {
                new CamelCaseNamingStrategy(),
                new IconSuffixNamingStrategy(new CamelCaseNamingStrategy()),
                new DefaultNamingStrategy()
        }));
        comboNaming.setRenderer(new NamingStrategyListCellRenderer());

        toolbar.add(new JLabel("Template:"), "right");
        toolbar.add(comboTemplates);
        toolbar.add(new JLabel("Naming:"), "right");
        toolbar.add(comboNaming);
        toolbar.add(label, "growx, push");
        toolbar.add(button, "width button");
        
        // Set the button action.
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JFileChooser chooser = new JFileChooser(lastDir);
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileFilter(new FileNameExtensionFilter("SVG images", "svg", "svgz"));
                int choice = chooser.showOpenDialog(button.getTopLevelAncestor());
                if (choice == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile().exists()) {
                    transcode(chooser.getSelectedFile());
                }
            }
        });
        
        return toolbar;
    }

    private void transcode(final File file) {
        lastDir = file.getParent();
        try {
            svgCanvas.setURI(file.toURI().toURL().toString());
            frame.setTitle(APPNAME + " - [" + file.getAbsolutePath() + "]");

            new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    setMessage("Transcoding...");

                    NamingStrategy namingStrategy = (NamingStrategy) comboNaming.getSelectedItem();

                    String svgClassName = namingStrategy.getClassName(file);

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
    
    public JComponent createCanvas() {
        JXPanel panel = new JXPanel(new BorderLayout());
        panel.setBackgroundPainter(new CheckerboardPainter(Color.WHITE, new Color(0xF0F0F0), 15));
        panel.add(svgCanvas);
        svgCanvas.setBackground(new Color(0, 0, 0, 0));
        
        // Set the JSVGCanvas listeners.
        svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
            @Override
            public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
                setMessage("Loading Document...");
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
            }
        });
        
        panel.setDropTarget(new DropTarget(panel, new DropTargetAdapter() {
            public void dragEnter(DropTargetDragEvent dtde) {
                List<File> files = getFiles(dtde.getTransferable());
                if (files.size() == 1 && (files.get(0).getName().endsWith(".svgz") || files.get(0).getName().endsWith(".svg"))) {
                    dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
                } else {
                    dtde.rejectDrag();
                }                
            }
            
            private List<File> getFiles(Transferable transferable) {
                if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    try {
                        return (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                return Collections.emptyList();
            }

            public void drop(DropTargetDropEvent dtde) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                transcode(getFiles(dtde.getTransferable()).get(0));
            }
        }));

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
