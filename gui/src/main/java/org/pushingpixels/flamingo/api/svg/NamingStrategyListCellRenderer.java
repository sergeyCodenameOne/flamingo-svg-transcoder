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
