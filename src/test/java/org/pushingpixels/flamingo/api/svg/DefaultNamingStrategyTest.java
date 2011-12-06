package org.pushingpixels.flamingo.api.svg;

import java.io.File;

import junit.framework.TestCase;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class DefaultNamingStrategyTest extends TestCase {
    
    public void testNaming() {
        NamingStrategy strategy = new DefaultNamingStrategy();
                
        assertEquals("edit_copy", strategy.getClassName(new File("edit-copy.svg")));
        assertEquals("edit_paste", strategy.getClassName(new File("edit paste.svgz")));
        assertEquals("edit_find", strategy.getClassName(new File("edit.find.svgz")));
        assertEquals("application_rss_xml", strategy.getClassName(new File("application-rss+xml.svg")));
        assertEquals("int_", strategy.getClassName(new File("int.svg")));
    }   
}
