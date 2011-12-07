package org.pushingpixels.flamingo.api.svg;

import junit.framework.TestCase;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TextSplitterTest extends TestCase {
    
    public void testInsert() {
        String code = "XXXXX XXX XXXXX".replaceAll("(.)", "$1\n");
        String separator = "${count}";
        
        assertEquals("XXXXX\nXXX\nXXXXX".replaceAll("(.)", "$1\n"), TextSplitter.insert(code, separator, 100));
        assertEquals("XXXXX1XXX2XXXXX".replaceAll("(.)", "$1\n"),   TextSplitter.insert(code, separator,   3));
        assertEquals("XXXXX1XXX2XXXXX".replaceAll("(.)", "$1\n"),   TextSplitter.insert(code, separator,   5));
        assertEquals("XXXXX\nXXX1XXXXX".replaceAll("(.)", "$1\n"),  TextSplitter.insert(code, separator,   8));
        assertEquals("XXXXX\nXXX1XXXXX".replaceAll("(.)", "$1\n"),  TextSplitter.insert(code, separator,  10));
    }
}
