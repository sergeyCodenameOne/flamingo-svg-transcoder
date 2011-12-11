/**
 * Copyright 2011 Emmanuel Bourg
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

package org.pushingpixels.flamingo.api.svg;

import java.io.File;

/**
 * Strategy for generating a class name from an image file.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public interface NamingStrategy {

    /**
     * Returns the name of the Icon class generated from the specified image file.
     * 
     * @param file the image
     */
    String getClassName(File file);
}
