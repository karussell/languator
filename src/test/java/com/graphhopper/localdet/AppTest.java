package com.graphhopper.localdet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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

    @Test
    public void testMyInit() throws Exception {
        List<String> list = Arrays.asList("Trento Centro", "Via Delle Bocchette");
        final Iterator<String> iter = list.iterator();
        MyLang lang = new App().myInit(new StringStream() {

            public boolean hasNext() {
                return iter.hasNext();
            }

            public String next() {
                return iter.next();
            }

            public String getName() {
                return "blup";
            }

            public int getCurrentSize() {
                return -1;
            }

        }, "it", 1);
        assertEquals("it", lang.toString());
        assertEquals(1, (int) lang.get("centr"));
    }
}
