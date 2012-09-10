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

import java.io.PrintWriter;

import org.apache.batik.ext.awt.RadialGradientPaint;

/**
 * Transcodes a radial gradient.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class RadialGradientPaintTranscoder extends MultipleGradientPaintTranscoder<RadialGradientPaint> {

    public static RadialGradientPaintTranscoder INSTANCE = new RadialGradientPaintTranscoder();

    @Override
    public void transcode(RadialGradientPaint paint, PrintWriter output) {
        StringBuilder colorsRep = new StringBuilder();
        if (paint.getFractions() == null) {
            colorsRep.append("null");
        } else {
            colorsRep.append(ColorArrayTranscoder.INSTANCE.transcode(paint.getColors()));
        }

        output.printf("new RadialGradientPaint(%s, %s, %s, %s, %s, %s, %s, %s)",
                PointTranscoder.INSTANCE.transcode(paint.getCenterPoint()),
                FloatTranscoder.INSTANCE.transcode(paint.getRadius()),
                PointTranscoder.INSTANCE.transcode(paint.getFocusPoint()),
                transcodeGradientFractions(paint.getFractions()),
                colorsRep.toString(),
                transcode(paint.getCycleMethod()),
                transcode(paint.getColorSpace()),
                AffineTransformTranscoder.INSTANCE.transcode(paint.getTransform()));
    }
}
