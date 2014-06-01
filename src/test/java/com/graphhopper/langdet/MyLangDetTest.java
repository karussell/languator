/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.graphhopper.langdet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        assertEquals("", instance.getLang("test"));
        assertEquals("", instance.getLang("15"));
        assertEquals("", instance.getLang("B"));
        assertEquals("de", instance.getLang("straße"));
        assertEquals("de", instance.getLang("riebeckstraße"));
        assertEquals("de", instance.getLang("Riebeckstraße"));
        assertEquals("de", instance.getLang("Erlanger Straße"));
        assertEquals("de", instance.getLang("amberg"));

        assertEquals("en", instance.getLang("menlo park"));
        assertEquals("en", instance.getLang("menlo abbey"));
        assertEquals("en", instance.getLang("Menlo Abbey"));
        assertEquals("en", instance.getLang("Sandringham Avenue"));
        assertEquals("en", instance.getLang("Gravelly Hill Interchange"));
        assertEquals("en", instance.getLang("Burgoyne's Road"));
    }

    @Test
    public void testGramOld() {
        MyLangDet instance = new MyLangDet();
        assertEquals("[test, te, tes]", instance.gramOld(t("test")).toString());
        assertEquals("[test, it, te, tes]", instance.gramOld(t("test it")).toString());
        assertEquals("[test, it, now, te, tes, no]", instance.gramOld(t("test it now")).toString());
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

    private List<String> t(String text) {
        return new ArrayList<String>(Arrays.asList(text.split(" ")));
    }
}
