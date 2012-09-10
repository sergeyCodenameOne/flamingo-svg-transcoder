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

import junit.framework.TestCase;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class MultipleGradientPaintTranscoderTest extends TestCase {

    public void testNormalizeFractions() throws Exception {
        float[] fractions = {0, 1f/3, 1f/3, 0.5f, 0.5f, 1};
        
        LinearGradientPaintTranscoder transcoder = new LinearGradientPaintTranscoder();
        float[] normalized = transcoder.normalizeFractions(fractions);
        
        assertNotNull(normalized);
        
        for (int i = 1; i < normalized.length; i++) {
            assertTrue(normalized[i] > normalized[i - 1]);
        }
    }
}
