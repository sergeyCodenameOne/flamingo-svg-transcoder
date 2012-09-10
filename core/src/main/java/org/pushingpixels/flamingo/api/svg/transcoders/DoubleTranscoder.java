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

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class DoubleTranscoder extends Transcoder<Double> {
    
    public static DoubleTranscoder INSTANCE = new DoubleTranscoder(); 

    @Override
    public void transcode(Double d, PrintWriter output) {
        if (Math.abs(Math.round(d) - d) < 0.000001) {
            output.append(String.valueOf(Math.round(d)));
        } else {
            output.append(String.valueOf(d));
        }
    }
}
