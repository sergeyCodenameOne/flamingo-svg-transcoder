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
    }   
}
