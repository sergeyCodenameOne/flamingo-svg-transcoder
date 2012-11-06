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
        Transcoder transcoder = null;
        Object object = shape;
        
        if (shape instanceof GeneralPath || shape instanceof ExtendedGeneralPath) {
            transcoder = PathIteratorTranscoder.INSTANCE;
            object = shape.getPathIterator(null);
        } else if (shape instanceof Rectangle2D) {
            transcoder = RectangleTranscoder.INSTANCE;
        } else if (shape instanceof RoundRectangle2D) {
            transcoder = RoundRectangleTranscoder.INSTANCE;
        } else if (shape instanceof Ellipse2D) {
            transcoder = EllipseTranscoder.INSTANCE;
        } else if (shape instanceof Line2D.Float) {
            transcoder = LineTranscoder.INSTANCE;
        }
        
        if (transcoder == null) {
            throw new UnsupportedOperationException(shape.getClass().getCanonicalName());            
        } else {
            transcoder.transcode(object, output);
        }
    }
}
