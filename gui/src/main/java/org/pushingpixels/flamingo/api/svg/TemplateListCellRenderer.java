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
 * ListCellRenderer for code generation templates.
 *
 * @author Emmanuel Bourg
 */
class TemplateListCellRenderer extends BasicListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String url = ((Template) value).getURL().toString();
        String label;
        if (url.contains("icon.template")) {
            label = "Swing Icon";
        } else if (url.contains("plain.template")) {
            label = "Plain Java2D";
        } else if (url.contains("resizable.template")) {
            label = "Flamingo Resizable Icon";
        } else {
            label = url.substring(url.lastIndexOf("/") + 1);
        }

        return super.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
    }
}
