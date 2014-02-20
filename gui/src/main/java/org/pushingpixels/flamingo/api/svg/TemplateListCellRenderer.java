package org.pushingpixels.flamingo.api.svg;

import java.awt.Component;
import javax.swing.JList;

/**
 * ListCellRenderer for code generation templates.
 * 
 * @author Emmanuel Bourg
 */
class TemplateListCellRenderer extends BasicListCellRenderer<Template> {

    public Component getListCellRendererComponent(JList list, Template value, int index, boolean isSelected, boolean cellHasFocus) {
        String url = value.getURL().toString();
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
