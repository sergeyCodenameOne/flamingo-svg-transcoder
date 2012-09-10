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
import java.awt.Transparency;
import java.io.PrintWriter;

/**
 * Transcodes a java.awt.Color instance. This transcoder assumes the class is statically imported in the generated class.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ColorTranscoder extends Transcoder<Color> {

    public static final ColorTranscoder INSTANCE = new ColorTranscoder();

    @Override
    public void transcode(Color color, PrintWriter output) {
        if (color.equals(Color.WHITE)) {
            output.append("WHITE");
        } else if (color.equals(Color.BLACK)) {
            output.append("BLACK");
        } else if (color.equals(Color.RED)) {
            output.append("RED");
        } else if (color.equals(Color.GREEN)) {
            output.append("GREEN");
        } else if (color.equals(Color.BLUE)) {
            output.append("BLUE");
        } else if (color.equals(Color.LIGHT_GRAY)) {
            output.append("LIGHT_GRAY");
        } else if (color.equals(Color.GRAY)) {
            output.append("GRAY");
        } else if (color.equals(Color.DARK_GRAY)) {
            output.append("DARK_GRAY");
        } else if (color.equals(Color.YELLOW)) {
            output.append("YELLOW");
        } else if (color.equals(Color.CYAN)) {
            output.append("CYAN");
        } else if (color.equals(Color.MAGENTA)) {
            output.append("MAGENTA");
        } else if (color.equals(Color.PINK)) {
            output.append("PINK");
        } else if (color.equals(Color.ORANGE)) {
            output.append("ORANGE");
        } else if (color.getTransparency() == Transparency.OPAQUE) {
            output.append("new Color(0x" + Integer.toHexString(color.getRGB()).toUpperCase().substring(2) + ")");
        } else {
            output.append("new Color(0x" + Integer.toHexString(color.getRGB()).toUpperCase() + ", true)");
        }
    }
}
