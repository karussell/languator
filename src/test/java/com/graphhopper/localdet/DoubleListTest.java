package com.graphhopper.localdet;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Karich
 */
public class DoubleListTest {

    @Test
    public void testToString() {
        IntList instance = new IntList();
        instance.set(2, 5);
        assertEquals(0, instance.get(0), 1e-4);
        assertEquals(5, instance.get(2), 1e-4);
        assertEquals("[0, 0, 5]", instance.toString());
    }
}
