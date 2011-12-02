package org.pushingpixels.flamingo.api.svg;

import java.io.File;

import junit.framework.TestCase;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class CamelCaseNamingStrategyTest extends TestCase {

    public void testNaming() {
        NamingStrategy strategy = new CamelCaseNamingStrategy();
        
        assertEquals("EditCopy", strategy.getClassName(new File("edit-copy.svg")));
        assertEquals("EditPaste", strategy.getClassName(new File("edit paste.svgz")));
        assertEquals("EditCut", strategy.getClassName(new File("edit__cut.svg")));
        assertEquals("EditFind", strategy.getClassName(new File("edit_find_.svg")));
    }
}
