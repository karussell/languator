package com.graphhopper.localdet;

import java.io.IOException;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

/**
 *
 * @author Peter Karich
 */
public class MyLangDetTest {

    @Test
    public void testGetLang() throws IOException {
        MyLangDet instance = new MyLangDet();
        instance.init();
//        assertEquals("", instance.getLang("test"));
        assertEquals("", instance.getLang("15"));
        assertEquals("", instance.getLang("100"));
        assertEquals("", instance.getLang("1000"));
        assertEquals("", instance.getLang("10000"));
        assertEquals("", instance.getLang("B"));
        assertEquals("de", instance.getLang("straße"));
        assertEquals("de", instance.getLang("riebeckstraße"));
        assertEquals("de", instance.getLang("Riebeckstraße"));
        assertEquals("de", instance.getLang("Erlanger Straße"));
        assertEquals("de", instance.getLang("amberg"));
        
//        assertEquals("en", instance.getLang("Menlo Park"));
        assertEquals("en", instance.getLang("menlo abbey"));
        assertEquals("en", instance.getLang("Menlo Abbey"));
        assertEquals("en", instance.getLang("Sandringham Avenue"));
        assertEquals("en", instance.getLang("Gravelly Hill Interchange"));        
        assertEquals("en", instance.getLang("Burgoyne's Road"));
        
        assertEquals("it", instance.getLang("Trento Centro"));
        assertEquals("it", instance.getLang("Via Delle Bocchette"));
        assertEquals("it", instance.getLang("San Michele all'Adige - Mezzocorona (Val di Non)"));
    }

    @Test
    public void testGram() {
        assertEquals("[test]", MyLangDet.gram("test", 4, 4).toString());
        assertEquals("[tes, test, est]", MyLangDet.gram("test", 3, 4).toString());
        assertEquals("[tes, test, est, esti, sti, stin, tin, ting, ing]", MyLangDet.gram("testing", 3, 4).toString());
    }

    @Test
    public void testTokenize() {
        MyLangDet instance = new MyLangDet();
        assertEquals("[test]", instance.tokenize("test").toString());
        assertEquals("[test, it]", instance.tokenize("test it").toString());
        assertEquals("[test, it, now]", instance.tokenize("test it now").toString());
    }
}
