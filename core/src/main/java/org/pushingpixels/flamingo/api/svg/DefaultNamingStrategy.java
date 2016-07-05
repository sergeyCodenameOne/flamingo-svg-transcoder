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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Keeps the name of the image file and replaces illegal characters in Java class names.
 *
 * For example edit-copy.svg is turned into edit_copy
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class DefaultNamingStrategy implements NamingStrategy {

    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
                    "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const","continue",
                    "default", "do", "double", "else", "enum", "extends", "final","finally", "float","for", "goto","if",
                    "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private",
                    "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized",
                    "this", "throw", "throws", "transient", "try", "void", "volatile", "while"));
    
    public String getClassName(File file) {
        String name = file.getName().substring(0, file.getName().lastIndexOf("."));
        name = name.replaceAll("[-+ .]", "_");
        if (isKeyword(name)) {
            name = name + "_";
        }
        if (Character.isDigit(name.charAt(0))) {
            name = "_" + name;
        }
        return name;
    }

    private boolean isKeyword(String s) {
        return KEYWORDS.contains(s);
    }
}
