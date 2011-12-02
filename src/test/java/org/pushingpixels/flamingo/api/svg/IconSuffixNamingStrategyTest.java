package org.pushingpixels.flamingo.api.svg;

import java.io.File;

import junit.framework.TestCase;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class IconSuffixNamingStrategyTest extends TestCase {

    public void testNaming() {
        NamingStrategy strategy = new IconSuffixNamingStrategy(new CamelCaseNamingStrategy());

        assertEquals("EditCopyIcon", strategy.getClassName(new File("edit-copy.svg")));
        assertEquals("EditPasteIcon", strategy.getClassName(new File("edit paste.svgz")));
        assertEquals("EditCutIcon", strategy.getClassName(new File("edit_cut_icon.svg")));
    }
}
