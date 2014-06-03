package com.graphhopper.languator;

import com.graphhopper.languator.MyLang;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Karich
 */
public class MyLangTest {

    @Test
    public void testNormalize() {
        assertEquals("plauen ", MyLang.normalize("plauen,"));
        // assertEquals("helm ", MyLang.normalize("helm'"));
    }
}
