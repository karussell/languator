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
        assertEquals("de", instance.getLang("amberg"));
    }

    @Test
    public void testGram() {
        MyLangDet instance = new MyLangDet();
        assertEquals("[test, te, tes]", instance.gram(t("test")).toString());
        assertEquals("[test, it, te, tes]", instance.gram(t("test it")).toString());
        assertEquals("[test, it, now, te, tes, no]", instance.gram(t("test it now")).toString());
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
