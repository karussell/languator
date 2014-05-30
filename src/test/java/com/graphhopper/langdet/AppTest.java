package com.graphhopper.langdet;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Karich
 */
public class AppTest {

    @Test
    public void testGetUsedMB() {
        assertEquals(1, Integer.parseInt(" 1".substring(1)));
        assertEquals(1, Integer.parseInt("A1".substring(1)));
    }

}
