package com.graphhopper.languator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Peter Karich
 */
public class MyLangDet {

    private final Map<String, IntList> langMap = new LinkedHashMap<String, IntList>();
    private final Map<String, Integer> addedLanguages = new LinkedHashMap<String, Integer>();
    private int minGram = Integer.MAX_VALUE;
    private int maxGram = 0;

    void init() {
        for (String tmpLang : new String[]{"de", "en", "it", "fr"}) {
            add(tmpLang, "myprofile/" + tmpLang + ".txt");
        }
    }

    public void add(String lang, String resource) {
        if (addedLanguages.containsKey(lang))
            throw new IllegalStateException("Cannot add language " + lang + " twice");

        MyLang myLang = new MyLang(lang);

        try {
            InputStream is = new FileInputStream(resource);
            if (is == null)
                throw new IllegalStateException("Cannot load " + resource);
            read(new InputStreamReader(is), myLang);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        int langIndex = addedLanguages.size();
        addedLanguages.put(lang, langIndex);

        if (myLang.getMinLength() < minGram)
            minGram = myLang.getMinLength();
        if (myLang.getMaxLength() > maxGram)
            maxGram = myLang.getMaxLength();

        for (Entry<String, Integer> e : myLang.getEntries()) {
            IntList list = langMap.get(e.getKey());
            if (list == null) {
                list = new IntList(addedLanguages.size());
                langMap.put(e.getKey(), list);
            }
            list.set(langIndex, e.getValue());
        }
    }

    List<LangPrio> getPrios(String text) {
        List<LangPrio> langPrios = new ArrayList<LangPrio>(addedLanguages.size());
        // assumption linked list
        for (Entry<String, Integer> e : addedLanguages.entrySet()) {
            langPrios.add(new LangPrio(e.getKey(), 0));
        }

        text = MyLang.normalize(text);
        for (String gram : gram(text, minGram, maxGram)) {
            IntList list = langMap.get(gram);
            if (list == null)
                continue;

            int len = list.size();
            for (int i = 0; i < len; i++) {
                int val = list.get(i);
                langPrios.get(i).prio += val * gram.length();
            }
        }
        Collections.sort(langPrios, prioComparator);
        return langPrios;
    }

    public String getLang(String text) {
        LangPrio lp = getPrios(text).get(0);
        if (lp.prio < 1)
            return "";
        return lp.lang;
    }

    List<String> tokenize(String text) {
        List<String> list = new ArrayList<String>();
        int edge = 0;
        for (int i = 1; i < text.length(); i++) {
            if (text.charAt(i) == ' ') {
                if (i - edge > 1)
                    list.add(text.substring(edge, i));
                edge = i + 1;
            }
        }
        if (text.length() - edge > 1)
            list.add(text.substring(edge));
        return list;
    }

    public static Collection<String> gram(String token, int fromLen, int toLen) {
        Set<String> tokens = new LinkedHashSet<String>();
        int len = token.length() - fromLen;
        for (int startIndex = 0; startIndex <= len; startIndex++) {
            int toIndex = Math.min(token.length(), startIndex + toLen);
            for (int endIndex = startIndex + fromLen; endIndex <= toIndex; endIndex++) {
                String str = token.substring(startIndex, endIndex);
                tokens.add(str);
            }
        }
        return tokens;
    }

    void read(Reader tmpReader, MyLang myLang) throws IOException {
        BufferedReader reader = new BufferedReader(tmpReader);
        String line;
        try {
            int lineNum = 0;
            try {
                while ((line = reader.readLine()) != null) {
                    lineNum++;
                    if (line.startsWith("//") || line.startsWith("#") || line.isEmpty())
                        continue;

                    String strs[] = line.split(",");
                    myLang.add(strs[0], Integer.parseInt(strs[1]));
                }
            } catch (Exception ex) {
                throw new RuntimeException("Error in line " + lineNum + ", " + ex.getMessage());
            }
        } finally {
            reader.close();
        }
    }

    static class LangPrio {

        String lang;
        double prio;

        public LangPrio(String lang, double prio) {
            this.lang = lang;
            this.prio = prio;
        }

        @Override
        public String toString() {
            return lang + ":" + prio;
        }
    }
    private static final Comparator<LangPrio> prioComparator = new Comparator<LangPrio>() {

        public int compare(LangPrio o1, LangPrio o2) {
            return -Double.compare(o1.prio, o2.prio);
        }
    };
}
