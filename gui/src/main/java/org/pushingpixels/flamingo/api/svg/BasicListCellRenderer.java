package org.pushingpixels.flamingo.api.svg;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * ListCellRenderer implementation that delegates the rendering to the renderer
 * defined by the current look and feel (unlike DefaultCellRenderer).
 * 
 * @author Emmanuel Bourg
 */
abstract class BasicListCellRenderer<E> implements ListCellRenderer<E> {
    private ListCellRenderer delegate;
    private Class uiClass;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (list.getUI().getClass() != uiClass) {
            uiClass = list.getUI().getClass();
            if ("ComboBox.list".equals(list.getName())) {
                delegate = new JComboBox().getRenderer();
            } else {
                delegate = new JList().getCellRenderer();
            }
        }
        
        return delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
