package com.graphhopper.localdet;

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
    }
}
