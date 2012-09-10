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

import java.awt.Color;
import java.awt.Paint;
import java.io.PrintWriter;

import org.apache.batik.ext.awt.LinearGradientPaint;
import org.apache.batik.ext.awt.RadialGradientPaint;

/**
 * Transcodes a paint.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class PaintTranscoder extends Transcoder<Paint> {

    public static PaintTranscoder INSTANCE = new PaintTranscoder();

    @Override
    public void transcode(Paint paint, PrintWriter output) {
        if (paint instanceof RadialGradientPaint) {
            RadialGradientPaintTranscoder.INSTANCE.transcode((RadialGradientPaint) paint, output);
        } else if (paint instanceof LinearGradientPaint) {
            LinearGradientPaintTranscoder.INSTANCE.transcode((LinearGradientPaint) paint, output);
        } else if (paint instanceof Color) {
            ColorTranscoder.INSTANCE.transcode((Color) paint, output);
        } else {
            throw new UnsupportedOperationException(paint.getClass().getCanonicalName());
        }
    }
}
