/**
 * Copyright 2014 Emmanuel Bourg
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

import java.awt.Component;
import javax.swing.JList;

/**
 * ListCellRenderer for the naming strategies.
 * 
 * @author Emmanuel Bourg
 */
class NamingStrategyListCellRenderer extends BasicListCellRenderer<NamingStrategy> {
    
    public Component getListCellRendererComponent(JList list, NamingStrategy value, int index, boolean isSelected, boolean cellHasFocus) {
        String label = null;
        if (value instanceof CamelCaseNamingStrategy) {
            label = "Camel Case";
        } else if (value instanceof IconSuffixNamingStrategy) {
            label = "Camel Case + 'Icon' suffix";
        } else if (value instanceof DefaultNamingStrategy) {
            label = "Same as input file";
        }
        return super.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
    }
}
