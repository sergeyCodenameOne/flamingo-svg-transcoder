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

import java.awt.geom.AffineTransform;
import java.io.PrintWriter;

/**
 * Transcodes an affine transformation.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class AffineTransformTranscoder extends Transcoder<AffineTransform> {
    
    public static AffineTransformTranscoder INSTANCE = new AffineTransformTranscoder();

    @Override
    public void transcode(AffineTransform transform, PrintWriter output) {
        if (transform.isIdentity()) {
            output.append("new AffineTransform()");
        } else {
            double[] matrix = new double[6];
            transform.getMatrix(matrix);

            output.append("new AffineTransform("
                    + FloatTranscoder.INSTANCE.transcode((float) matrix[0]) + ", " + FloatTranscoder.INSTANCE.transcode((float) matrix[1]) + ", "
                    + FloatTranscoder.INSTANCE.transcode((float) matrix[2]) + ", " + FloatTranscoder.INSTANCE.transcode((float) matrix[3]) + ", "
                    + FloatTranscoder.INSTANCE.transcode((float) matrix[4]) + ", " + FloatTranscoder.INSTANCE.transcode((float) matrix[5]) + ")");
        }
    }
}
