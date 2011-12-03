/*
 * Copyright (c) 2005-2010 Flamingo Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of Flamingo Kirill Grouchnikov nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

package org.pushingpixels.flamingo.api.svg;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Formatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.ext.awt.LinearGradientPaint;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.ext.awt.MultipleGradientPaint.ColorSpaceEnum;
import org.apache.batik.ext.awt.MultipleGradientPaint.CycleMethodEnum;
import org.apache.batik.ext.awt.RadialGradientPaint;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.CompositeShapePainter;
import org.apache.batik.gvt.FillShapePainter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.gvt.StrokeShapePainter;
import org.w3c.dom.Document;

/**
 * SVG to Java2D transcoder.
 *
 * @author Kirill Grouchnikov.
 */
public class SvgTranscoder {
    /** Listener. */
    protected TranscoderListener listener;

    /** Print writer that wraps the {@link TranscoderListener#getWriter()} of the registered {@link #listener}. */
    protected PrintWriter externalPrintWriter;

    /** Print writer that wraps the {@link TranscoderListener#getWriter()} of the registered {@link #listener}. */
    protected PrintWriter printWriter;

    /** Class name for the generated Java2D code. */
    protected String javaClassName;

    /** Package name for the generated Java2D code. */
    protected String javaPackageName;

    protected boolean javaToImplementResizableIconInterface;

    protected final static String TOKEN_PACKAGE = "TOKEN_PACKAGE";
    protected final static String TOKEN_CLASSNAME = "TOKEN_CLASSNAME";
    protected final static String TOKEN_PAINTING_CODE = "TOKEN_PAINTING_CODE";
    protected final static String TOKEN_ORIG_X = "TOKEN_ORIG_X";
    protected final static String TOKEN_ORIG_Y = "TOKEN_ORIG_Y";
    protected final static String TOKEN_ORIG_WIDTH = "TOKEN_ORIG_WIDTH";
    protected final static String TOKEN_ORIG_HEIGHT = "TOKEN_ORIG_HEIGHT";

    /** URI of the SVG image. */
    protected String uri;

    /** Batik bridge context. */
    private BridgeContext batikBridgeContext;

    /** The current composite. */
    private AlphaComposite currentComposite;

    /** The current paint. */
    private Paint currentPaint;

    /**
     * Creates a new transcoder.
     *
     * @param uri           URI of the SVG image.
     * @param javaClassname Classname for the generated Java2D code.
     */
    public SvgTranscoder(String uri, String javaClassname) {
        this(javaClassname);
        this.uri = uri;
    }

    /**
     * Transcodes the SVG image into Java2D code. Does nothing if the
     * {@link #listener} is <code>null</code>.
     */
    public void transcode() {
        if (this.listener == null) {
            return;
        }

        UserAgentAdapter ua = new UserAgentAdapter();
        DocumentLoader loader = new DocumentLoader(ua);
        batikBridgeContext = new BridgeContext(ua, loader);
        batikBridgeContext.setDynamicState(BridgeContext.DYNAMIC);
        ua.setBridgeContext(batikBridgeContext);

        GVTBuilder builder = new GVTBuilder();
        Document svgDoc;
        try {
            svgDoc = loader.loadDocument(this.uri);
            GraphicsNode gvtRoot = builder.build(batikBridgeContext, svgDoc);

            this.transcode(gvtRoot);
        } catch (IOException ex) {
            Logger.getLogger(SvgTranscoder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Creates a new transcoder.
     *
     * @param uri           URI of the SVG image.
     * @param javaClassname Classname for the generated Java2D code.
     */
    public SvgTranscoder(String javaClassname) {
        this.javaClassName = javaClassname;
        this.javaToImplementResizableIconInterface = false;
    }

    public void setJavaToImplementResizableIconInterface(boolean javaToImplementResizableIconInterface) {
        this.javaToImplementResizableIconInterface = javaToImplementResizableIconInterface;
    }

    public void setJavaPackageName(String javaPackageName) {
        this.javaPackageName = javaPackageName;
    }

    /**
     * Sets the listener.
     *
     * @param listener Listener.
     */
    public void setListener(TranscoderListener listener) {
        this.listener = listener;
        this.setPrintWriter(new PrintWriter(this.listener.getWriter()));
    }

    public void setPrintWriter(PrintWriter printWriter) {
        this.externalPrintWriter = printWriter;
    }

    /**
     * Transcodes the SVG image into Java2D code.
     */
    public void transcode(GraphicsNode gvtRoot) {
        ByteArrayOutputStream paintingCodeStream = new ByteArrayOutputStream();
        this.printWriter = new IndentingPrintWriter(new PrintWriter(paintingCodeStream));
        transcodeGraphicsNode(gvtRoot, "");
        this.printWriter.close();

        String templateString = readTemplate("SvgTranscoderTemplate" + (javaToImplementResizableIconInterface ? "Resizable" : "Plain") + ".templ");
        
        templateString = templateString.replaceAll(TOKEN_PACKAGE, javaPackageName != null ? "package " + javaPackageName + ";" : "");
        templateString = templateString.replaceAll(TOKEN_CLASSNAME, javaClassName);

        String paintingCode = new String(paintingCodeStream.toByteArray());
        templateString = templateString.replaceAll(TOKEN_PAINTING_CODE, paintingCode);

        Rectangle2D bounds = gvtRoot.getBounds();

        templateString = templateString.replaceAll(TOKEN_ORIG_X, "" + (int) Math.ceil(bounds.getX()));
        templateString = templateString.replaceAll(TOKEN_ORIG_Y, "" + (int) Math.ceil(bounds.getY()));
        templateString = templateString.replaceAll(TOKEN_ORIG_WIDTH, "" + (int) Math.ceil(bounds.getWidth()));
        templateString = templateString.replaceAll(TOKEN_ORIG_HEIGHT, "" + (int) Math.ceil(bounds.getHeight()));

        this.externalPrintWriter.println(templateString);
        this.externalPrintWriter.close();

        if (listener != null) {
            listener.finished();
        }
    }

    private String readTemplate(String name) {
        InputStream in = SvgTranscoder.class.getResourceAsStream(name);
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                buffer.append(line + "\n");
            }
            reader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        return buffer.toString();
    }

    /**
     * Transcodes the specified path iterator.
     *
     * @param pathIterator Path iterator.
     */
    private void transcodePathIterator(PathIterator pathIterator) {
        float[] coords = new float[6];
        printWriter.println("shape = new GeneralPath();");
        for (; !pathIterator.isDone(); pathIterator.next()) {
            int type = pathIterator.currentSegment(coords);
            switch (type) {
                case PathIterator.SEG_CUBICTO:
                    printWriter.println("((GeneralPath) shape).curveTo(" + coords[0] + ", " + coords[1] + ", " + coords[2] + ", " + coords[3] + ", " + coords[4] + ", " + coords[5] + ");");
                    break;
                case PathIterator.SEG_QUADTO:
                    printWriter.println("((GeneralPath) shape).quadTo(" + coords[0] + ", " + coords[1] + ", " + coords[2] + ", " + coords[3] + ");");
                    break;
                case PathIterator.SEG_MOVETO:
                    printWriter.println("((GeneralPath) shape).moveTo(" + coords[0] + ", " + coords[1] + ");");
                    break;
                case PathIterator.SEG_LINETO:
                    printWriter.println("((GeneralPath) shape).lineTo(" + coords[0] + ", " + coords[1] + ");");
                    break;
                // through
                case PathIterator.SEG_CLOSE:
                    printWriter.println("((GeneralPath) shape).closePath();");
                    break;
            }
        }
    }

    /**
     * Transcodes the specified shape.
     *
     * @param shape Shape.
     * @throws UnsupportedOperationException if the shape is unsupported.
     */
    private void transcodeShape(Shape shape) throws UnsupportedOperationException {
        if (shape instanceof ExtendedGeneralPath) {
            transcodePathIterator(((ExtendedGeneralPath) shape).getPathIterator(null));
            
        } else if (shape instanceof GeneralPath) {
            transcodePathIterator(((GeneralPath) shape).getPathIterator(null));
            
        } else if (shape instanceof Rectangle2D) {
            Rectangle2D rect = (Rectangle2D) shape;
            printWriter.println("shape = new Rectangle2D.Double(" + rect.getX() + ", " + rect.getY() + ", " + rect.getWidth() + ", " + rect.getHeight() + ");");
            
        } else if (shape instanceof RoundRectangle2D) {
            RoundRectangle2D rRect = (RoundRectangle2D) shape;
            printWriter.println("shape = new RoundRectangle2D.Double("
                    + rRect.getX() + ", " + rRect.getY() + ", "
                    + rRect.getWidth() + ", " + rRect.getHeight() + ", "
                    + rRect.getArcWidth() + ", " + rRect.getArcHeight() + ");");
            
        } else if (shape instanceof Ellipse2D) {
            Ellipse2D ell = (Ellipse2D) shape;
            printWriter.println("shape = new Ellipse2D.Double(" + ell.getX() + ", " + ell.getY() + ", " + ell.getWidth() + ", " + ell.getHeight() + ");");
            
        } else if (shape instanceof Line2D.Float) {
            Line2D.Float l2df = (Line2D.Float) shape;
            printWriter.format("shape = new Line2D.Float(%ff, %ff, %ff, %ff);\n", l2df.x1, l2df.y1, l2df.x2, l2df.y2);
            
        } else {
            throw new UnsupportedOperationException(shape.getClass().getCanonicalName());
        }
    }

    /**
     * Transcodes the specified linear gradient paint.
     *
     * @param paint Linear gradient paint.
     * @throws IllegalArgumentException if the fractions are not strictly increasing.
     */
    private String transcodeLinearGradientPaint(LinearGradientPaint paint) throws IllegalArgumentException {
        float[] fractions = paint.getFractions();
        CycleMethodEnum cycleMethod = paint.getCycleMethod();
        ColorSpaceEnum colorSpace = paint.getColorSpace();
        AffineTransform transform = paint.getTransform();

        float previousFraction = -1.0f;
        for (float currentFraction : fractions) {
            if (currentFraction < 0f || currentFraction > 1f) {
                throw new IllegalArgumentException("Fraction values must " + "be in the range 0 to 1: " + currentFraction);
            }

            if (currentFraction < previousFraction) {
                throw new IllegalArgumentException("Keyframe fractions " + "must be non-decreasing: " + currentFraction);
            }

            previousFraction = currentFraction;
        }

        StringBuilder fractionsRep = new StringBuilder();
        if (fractions == null) {
            fractionsRep.append("null");
        } else {
            String sep = "";
            fractionsRep.append("new float[]{");
            previousFraction = -1.0f;
            for (float currentFraction : fractions) {
                fractionsRep.append(sep);
                if (currentFraction == previousFraction) {
                    currentFraction += 0.000000001f;
                }
                fractionsRep.append(transcodeFloat(currentFraction));
                sep = ", ";

                previousFraction = currentFraction;
            }
            fractionsRep.append("}");
        }

        StringBuilder colorsRep = new StringBuilder();
        if (fractions == null) {
            colorsRep.append("null");
        } else {
            colorsRep.append(transcodeArray(paint.getColors()));
        }

        String cycleMethodRep = null;
        if (cycleMethod == MultipleGradientPaint.NO_CYCLE) {
            cycleMethodRep = "MultipleGradientPaint.CycleMethod.NO_CYCLE";
        } else if (cycleMethod == MultipleGradientPaint.REFLECT) {
            cycleMethodRep = "MultipleGradientPaint.CycleMethod.REFLECT";
        } else if (cycleMethod == MultipleGradientPaint.REPEAT) {
            cycleMethodRep = "MultipleGradientPaint.CycleMethod.REPEAT";
        }

        String colorSpaceRep = null;
        if (colorSpace == MultipleGradientPaint.SRGB) {
            colorSpaceRep = "MultipleGradientPaint.ColorSpaceType.SRGB";
        } else if (colorSpace == MultipleGradientPaint.LINEAR_RGB) {
            colorSpaceRep = "MultipleGradientPaint.ColorSpaceType.LINEAR_RGB";
        }
        
        return new Formatter().format("new LinearGradientPaint(%s, %s, %s, %s, %s, %s, %s)", 
                transcodePoint(paint.getStartPoint()),
                transcodePoint(paint.getEndPoint()),
                fractionsRep.toString(),
                colorsRep.toString(),
                cycleMethodRep, colorSpaceRep,
                transcodeTransform(transform)).toString();
    }

    /**
     * Transcodes the specified radial gradient paint.
     *
     * @param paint Radial gradient paint.
     * @throws IllegalArgumentException if the fractions are not strictly increasing.
     */
    private String transcodeRadialGradientPaint(RadialGradientPaint paint) throws IllegalArgumentException {
        float[] fractions = paint.getFractions();
        CycleMethodEnum cycleMethod = paint.getCycleMethod();
        ColorSpaceEnum colorSpace = paint.getColorSpace();
        
        float previousFraction = -1.0f;
        for (float currentFraction : fractions) {
            if (currentFraction < 0f || currentFraction > 1f) {
                throw new IllegalArgumentException("Fraction values must " + "be in the range 0 to 1: " + currentFraction);
            }

            if (currentFraction < previousFraction) {
                throw new IllegalArgumentException("Keyframe fractions " + "must be non-decreasing: " + currentFraction);
            }

            previousFraction = currentFraction;
        }

        StringBuilder fractionsRep = new StringBuilder();
        if (fractions == null) {
            fractionsRep.append("null");
        } else {
            String sep = "";
            fractionsRep.append("new float[]{");
            previousFraction = -1.0f;
            for (float currentFraction : fractions) {
                fractionsRep.append(sep);
                if (currentFraction == previousFraction) {
                    currentFraction += 0.000000001f;
                }
                fractionsRep.append(transcodeFloat(currentFraction));
                sep = ", ";

                previousFraction = currentFraction;
            }
            fractionsRep.append("}");
        }

        StringBuilder colorsRep = new StringBuilder();
        if (fractions == null) {
            colorsRep.append("null");
        } else {
            colorsRep.append(transcodeArray(paint.getColors()));
        }

        String cycleMethodRep = null;
        if (cycleMethod == MultipleGradientPaint.NO_CYCLE) {
            cycleMethodRep = "MultipleGradientPaint.CycleMethod.NO_CYCLE";
        } else if (cycleMethod == MultipleGradientPaint.REFLECT) {
            cycleMethodRep = "MultipleGradientPaint.CycleMethod.REFLECT";
        } else if (cycleMethod == MultipleGradientPaint.REPEAT) {
            cycleMethodRep = "MultipleGradientPaint.CycleMethod.REPEAT";
        }

        String colorSpaceRep = null;
        if (colorSpace == MultipleGradientPaint.SRGB) {
            colorSpaceRep = "MultipleGradientPaint.ColorSpaceType.SRGB";
        } else if (colorSpace == MultipleGradientPaint.LINEAR_RGB) {
            colorSpaceRep = "MultipleGradientPaint.ColorSpaceType.LINEAR_RGB";
        }
        
        return "new RadialGradientPaint("
                + transcodePoint(paint.getCenterPoint()) + ", " + transcodeFloat(paint.getRadius()) + ", " + transcodePoint(paint.getFocusPoint()) + ", "
                + fractionsRep.toString() + ", " + colorsRep.toString()
                + ", " + cycleMethodRep + ", " + colorSpaceRep
                + ", " + transcodeTransform(paint.getTransform()) + ")";
    }

    /**
     * Transcodes the specified point.
     * 
     * @param point
     */
    private String transcodePoint(Point2D point) {
        return "new Point2D.Double(" + point.getX() + ", " + point.getY() + ")";
    }

    /**
     * Transcodes the specified transform.
     * 
     * @param transform
     */
    private String transcodeTransform(AffineTransform transform) {
        double[] matrix = new double[6];
        transform.getMatrix(matrix);
        
        return "new AffineTransform("
                + matrix[0] + "f, " + matrix[1] + "f, "
                + matrix[2] + "f, " + matrix[3] + "f, "
                + matrix[4] + "f, " + matrix[5] + "f)";
    }

    /**
     * Transcodes the specified color.
     * 
     * @param color
     */
    private String transcodeColor(Color color) {
        if (color.equals(Color.WHITE)) {
            return "Color.WHITE";
        } else if (color.equals(Color.BLACK)) {
            return "Color.BLACK";
        } else if (color.equals(Color.RED)) {
            return "Color.RED";
        } else if (color.equals(Color.GREEN)) {
            return "Color.GREEN";
        } else if (color.equals(Color.BLUE)) {
            return "Color.BLUE";
        } else if (color.equals(Color.LIGHT_GRAY)) {
            return "Color.LIGHT_GRAY";
        } else if (color.equals(Color.GRAY)) {
            return "Color.GRAY";
        } else if (color.equals(Color.DARK_GRAY)) {
            return "Color.DARK_GRAY";
        } else if (color.equals(Color.YELLOW)) {
            return "Color.YELLOW";
        } else if (color.equals(Color.CYAN)) {
            return "Color.CYAN";
        } else if (color.equals(Color.MAGENTA)) {
            return "Color.MAGENTA";
        } else if (color.getTransparency() == Transparency.OPAQUE) {
            return "new Color(0x" + Integer.toHexString(color.getRGB()).toUpperCase().substring(2) + ")";
        } else {
            return "new Color(0x" + Integer.toHexString(color.getRGB()).toUpperCase() + ", true)";
        }
    }

    /**
     * Transcodes the specified array.
     * 
     * @param colors
     */
    private String transcodeArray(Color[] colors) {
        String sep = "";
        StringBuilder builder = new StringBuilder();
        builder.append("new Color[]{");
        for (Color color : colors) {
            builder.append(sep);
            builder.append(transcodeColor(color));
            sep = ", ";
        }
        builder.append("}");

        return builder.toString();
    }

    /**
     * Transcodes the specified paint.
     *
     * @param paint Paint.
     * @throws UnsupportedOperationException if the paint is unsupported.
     */
    private String transcodePaint(Paint paint) throws UnsupportedOperationException {
        if (paint instanceof RadialGradientPaint) {
            return transcodeRadialGradientPaint((RadialGradientPaint) paint);
        } else if (paint instanceof LinearGradientPaint) {
            return transcodeLinearGradientPaint((LinearGradientPaint) paint);
        } else if (paint instanceof Color) {
            return transcodeColor((Color) paint);
        } else {
            throw new UnsupportedOperationException(paint.getClass().getCanonicalName());
        }
    }

    /**
     * Transcodes the specified stroke.
     * 
     * @param stroke
     */
    private String transcodeStroke(BasicStroke stroke) {
        StringBuilder dashRep = new StringBuilder();
        if (stroke.getDashArray() == null) {
            dashRep.append("null");
        } else {
            String sep = "";
            dashRep.append("new float[]{");
            for (float dash : stroke.getDashArray()) {
                dashRep.append(sep);
                dashRep.append(transcodeFloat(dash));
                sep = ", ";
            }
            dashRep.append("}");
        }
        
        return "new BasicStroke(" + transcodeFloat(stroke.getLineWidth()) + ", " + stroke.getEndCap() + ", " 
                + stroke.getLineJoin() + ", " + transcodeFloat(stroke.getMiterLimit()) + ", "
                + dashRep + ", " + transcodeFloat(stroke.getDashPhase()) + ")";
    }

    /**
     * Transcode the specified float value.
     * 
     * @return
     */
    private String transcodeFloat(float f) {
        if (Math.abs(Math.round(f) - f) < 0.000001) {
            return String.valueOf(Math.round(f));
        } else {
            return String.valueOf(f) + "f";
        }
    }

    /**
     * Transcodes the specified shape painter.
     *
     * @param painter Shape painter.
     * @throws UnsupportedOperationException if the shape painter is unsupported.
     */
    private void transcodeShapePainter(ShapePainter painter) throws UnsupportedOperationException {
        if (painter instanceof CompositeShapePainter) {
            transcodeCompositeShapePainter((CompositeShapePainter) painter);
        } else if (painter instanceof FillShapePainter) {
            transcodeFillShapePainter((FillShapePainter) painter);
        } else if (painter instanceof StrokeShapePainter) {
            transcodeStrokeShapePainter((StrokeShapePainter) painter);
        } else if (painter != null) {
            throw new UnsupportedOperationException(painter.getClass().getCanonicalName());
        }
    }

    /**
     * Transcodes the specified composite shape painter.
     *
     * @param painter Composite shape painter.
     */
    private void transcodeCompositeShapePainter(CompositeShapePainter painter) {
        for (int i = 0; i < painter.getShapePainterCount(); i++) {
            transcodeShapePainter(painter.getShapePainter(i));
        }
    }

    /**
     * Transcodes the specified fill shape painter.
     *
     * @param painter Fill shape painter.
     */
    private void transcodeFillShapePainter(FillShapePainter painter) {
        Paint paint = (Paint) painter.getPaint();
        if (paint == null) {
            return;
        }
        
        transcodeShape(painter.getShape());
        
        if (!paint.equals(currentPaint)) {
            currentPaint = paint;
            printWriter.println("g.setPaint(" + transcodePaint(paint) + ");");
        }
        printWriter.println("g.fill(shape);");
    }

    /**
     * Transcodes the specified stroke shape painter.
     *
     * @param painter Stroke shape painter.
     */
    private void transcodeStrokeShapePainter(StrokeShapePainter painter) {
        Shape shape = painter.getShape();
        Paint paint = (Paint) painter.getPaint();
        if (paint == null) {
            return;
        }
        
        transcodeShape(shape);
        
        if (!paint.equals(currentPaint)) {
            currentPaint = paint;
            printWriter.println("g.setPaint(" + transcodePaint(paint) + ");");
        }
        printWriter.println("g.setStroke(" + transcodeStroke((BasicStroke) painter.getStroke()) + ");");
        printWriter.println("g.draw(shape);");
    }

    /**
     * Transcodes the specified shape node.
     *
     * @param node    Shape node.
     * @param comment Comment (for associating the Java2D section with the corresponding SVG section).
     */
    private void transcodeShapeNode(ShapeNode node, String comment) {
        transcodeShapePainter(node.getShapePainter());
    }

    /**
     * Transcodes the specified composite graphics node.
     *
     * @param node    Composite graphics node.
     * @param comment Comment (for associating the Java2D section with the corresponding SVG section).
     */
    private void transcodeCompositeGraphicsNode(CompositeGraphicsNode node, String comment) {
        int count = 0;
        for (Object obj : node.getChildren()) {
            transcodeGraphicsNode((GraphicsNode) obj, comment + "_" + count);
            count++;
        }
    }

    /**
     * Transcodes the specified graphics node.
     *
     * @param node    Graphics node.
     * @param comment Comment (for associating the Java2D section with the corresponding SVG section).
     * @throws UnsupportedOperationException if the graphics node is unsupported.
     */
    private void transcodeGraphicsNode(GraphicsNode node, String comment) throws UnsupportedOperationException {
        AlphaComposite composite = (AlphaComposite) node.getComposite();
        if (composite != null && !composite.equals(currentComposite)) {
            currentComposite = composite;
            printWriter.println("g.setComposite(AlphaComposite.getInstance(" + composite.getRule() + ", " + transcodeFloat(composite.getAlpha()) + " * origAlpha));");
        }
        
        AffineTransform transform = node.getTransform();
        if (transform != null && !transform.isIdentity()) {
            printWriter.println("AffineTransform defaultTransform_" + comment + " = g.getTransform();");
            printWriter.println("g.transform(" + transcodeTransform(transform) + ");");
        }
        
        try {
            printWriter.println("");
            printWriter.println("// " + comment);
            if (node instanceof ShapeNode) {
                transcodeShapeNode((ShapeNode) node, comment);
            } else if (node instanceof CompositeGraphicsNode) {
                transcodeCompositeGraphicsNode((CompositeGraphicsNode) node, comment);
            } else {
                throw new UnsupportedOperationException(node.getClass().getCanonicalName());
            }
        } finally {
            if (transform != null && !transform.isIdentity()) {
                printWriter.println("g.setTransform(defaultTransform_" + comment + ");");
            }
        }
    }
}
