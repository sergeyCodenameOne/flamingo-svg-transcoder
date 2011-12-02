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
 * Keeps the name of the image file and replaces illegal characters in Java class names.
 *
 * For example edit-copy.svg is turned into edit_copy.java
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class DefaultNamingStrategy implements NamingStrategy {

    public String getClassName(File file) {
        String name = file.getName().substring(0, file.getName().lastIndexOf("."));
        return name.replaceAll("[- ]", "_");
    }
}
