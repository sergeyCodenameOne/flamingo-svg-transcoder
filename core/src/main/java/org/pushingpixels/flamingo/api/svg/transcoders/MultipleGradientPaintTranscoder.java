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

import org.apache.batik.ext.awt.MultipleGradientPaint;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class MultipleGradientPaintTranscoder<P extends MultipleGradientPaint> extends Transcoder<P> {

    protected String transcode(MultipleGradientPaint.CycleMethodEnum cycleMethod) {
        if (cycleMethod == MultipleGradientPaint.NO_CYCLE) {
            return "NO_CYCLE";
        } else if (cycleMethod == MultipleGradientPaint.REFLECT) {
            return "REFLECT";
        } else if (cycleMethod == MultipleGradientPaint.REPEAT) {
            return "REPEAT";
        } else {
            throw new IllegalArgumentException("Unknown cycle method: " + cycleMethod);
        }
    }

    protected String transcode(MultipleGradientPaint.ColorSpaceEnum colorSpace) {
        if (colorSpace == MultipleGradientPaint.SRGB) {
            return "SRGB";
        } else if (colorSpace == MultipleGradientPaint.LINEAR_RGB) {
            return "LINEAR_RGB";
        } else {
            throw new IllegalArgumentException("Unknown color space: " + colorSpace);
        }
    }

    protected String transcodeGradientFractions(float[] fractions) {
        float previousFraction = -1.0f;
        for (float currentFraction : fractions) {
            if (currentFraction < 0f || currentFraction > 1f) {
                throw new IllegalArgumentException("Fraction values must be in the range 0 to 1: " + currentFraction);
            }

            if (currentFraction < previousFraction) {
                throw new IllegalArgumentException("Keyframe fractions must be non-decreasing: " + currentFraction);
            }

            previousFraction = currentFraction;
        }

        StringBuilder builder = new StringBuilder();
        if (fractions == null) {
            builder.append("null");
        } else {
            String sep = "";
            builder.append("new float[]{");
            previousFraction = -1.0f;
            for (float fraction : fractions) {
                builder.append(sep);
                if (fraction == previousFraction) {
                    fraction += 0.000000001f;
                }
                if (fraction == 0 || fraction == 1) {
                    builder.append((int) fraction);
                } else {
                    builder.append(fraction).append("f");
                }
                sep = ", ";

                previousFraction = fraction;
            }
            builder.append("}");
        }

        return builder.toString();
    }
}
