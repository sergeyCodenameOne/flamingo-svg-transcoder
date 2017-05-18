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
            output.append("0xffffff");
        } else if (color.equals(Color.BLACK)) {
            output.append("0");
        } else if (color.equals(Color.RED)) {
            output.append("0xff0000");
        } else if (color.equals(Color.GREEN)) {
            output.append("0xff00");
        } else if (color.equals(Color.BLUE)) {
            output.append("0xff");
        } else if (color.equals(Color.LIGHT_GRAY)) {
            output.append("0xc0c0c0");
        } else if (color.equals(Color.GRAY)) {
            output.append("0x808080");
        } else if (color.equals(Color.DARK_GRAY)) {
            output.append("0x404040");
        } else if (color.equals(Color.YELLOW)) {
            output.append("0xffff00");
        } else if (color.equals(Color.CYAN)) {
            output.append("0xffff");
        } else if (color.equals(Color.MAGENTA)) {
            output.append("0xff00ff");
        } else if (color.equals(Color.PINK)) {
            output.append("0xffafaf");
        } else if (color.equals(Color.ORANGE)) {
            output.append("0x255c800");
        } else if (color.getTransparency() == Transparency.OPAQUE) {
            output.append("0x" + Integer.toHexString(color.getRGB()).toUpperCase().substring(2) + "");
        } else {
            output.append("0x" + Integer.toHexString(color.getRGB()).toUpperCase());
        }
    }
}
