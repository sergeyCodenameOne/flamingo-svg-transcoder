/**
 * Copyright 2012 Emmanuel Bourg
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

package org.pushingpixels.flamingo.api.svg.transcoders;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.PrintWriter;
import java.util.Locale;

import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;

/**
 * Transcodes a shape.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ShapeTranscoder extends Transcoder<Shape> {

    public static ShapeTranscoder INSTANCE = new ShapeTranscoder();

    @Override
    public void transcode(Shape shape, PrintWriter output) {
        if (shape instanceof ExtendedGeneralPath) {
            PathIteratorTranscoder.INSTANCE.transcode(((ExtendedGeneralPath) shape).getPathIterator(null), output);
            
        } else if (shape instanceof GeneralPath) {
            PathIteratorTranscoder.INSTANCE.transcode(((GeneralPath) shape).getPathIterator(null), output);
            
        } else if (shape instanceof Rectangle2D) {
            Rectangle2D rect = (Rectangle2D) shape;
            output.println("shape = new Rectangle2D.Double("
                    + DoubleTranscoder.INSTANCE.transcode(rect.getX()) + ", " + DoubleTranscoder.INSTANCE.transcode(rect.getY()) + ", "
                    + DoubleTranscoder.INSTANCE.transcode(rect.getWidth()) + ", " + DoubleTranscoder.INSTANCE.transcode(rect.getHeight()) + ");");
            
        } else if (shape instanceof RoundRectangle2D) {
            RoundRectangle2D rRect = (RoundRectangle2D) shape;
            output.println("shape = new RoundRectangle2D.Double("
                    + DoubleTranscoder.INSTANCE.transcode(rRect.getX()) + ", " + DoubleTranscoder.INSTANCE.transcode(rRect.getY()) + ", "
                    + DoubleTranscoder.INSTANCE.transcode(rRect.getWidth()) + ", " + DoubleTranscoder.INSTANCE.transcode(rRect.getHeight()) + ", "
                    + DoubleTranscoder.INSTANCE.transcode(rRect.getArcWidth()) + ", " + DoubleTranscoder.INSTANCE.transcode(rRect.getArcHeight()) + ");");
            
        } else if (shape instanceof Ellipse2D) {
            Ellipse2D ell = (Ellipse2D) shape;
            output.println("shape = new Ellipse2D.Double(" + ell.getX() + ", " + ell.getY() + ", " + ell.getWidth() + ", " + ell.getHeight() + ");");
            
        } else if (shape instanceof Line2D.Float) {
            Line2D.Float l2df = (Line2D.Float) shape;
            output.format(Locale.ENGLISH, "shape = new Line2D.Float(%ff, %ff, %ff, %ff);\n", l2df.x1, l2df.y1, l2df.x2, l2df.y2);
            
        } else {
            throw new UnsupportedOperationException(shape.getClass().getCanonicalName());
        }
    }
}
