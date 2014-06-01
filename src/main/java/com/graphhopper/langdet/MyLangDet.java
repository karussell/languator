package com.graphhopper.langdet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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

    private final Map<String, MyLang> langSet = new LinkedHashMap<String, MyLang>();

    public void init() throws IOException {
        for (String lang : new String[]{"en", "de"}) {
            add(lang, "lang_det_" + lang + ".txt, decompound_" + lang + ".txt");
        }
    }

    public void add(String lang, String resources) throws IOException {
        MyLang set = new MyLang(lang);
        for (String resource : resources.split(",")) {
            resource = resource.trim();
            InputStream is = getClass().getResourceAsStream(resource);
            if (is != null)
                set.addAll(read(new InputStreamReader(is)));
        }
        set = langSet.put(lang, set);
        if (set != null)
            throw new IllegalStateException("language " + lang + " already exists");
    }

    public String getLang(String text) {
        List<LangPrio> map = getProbabilities(text);
        if (map.isEmpty())
            return "";

        return map.get(0).lang;
    }

    List<LangPrio> getProbabilities(String text) {
        List<LangPrio> list = new ArrayList<LangPrio>();
        text = text.toLowerCase();
        for (Entry<String, MyLang> e : langSet.entrySet()) {
            String lang = e.getKey();
            MyLang myLang = e.getValue();
            double num = 0;
            for (String token : tokenize(text)) {
                for (String gram : gram(token, myLang.getMinLength(), myLang.getMaxLength())) {
                    if (myLang.contains(gram)) {
                        if (token.length() == gram.length())
                            num += 2 * gram.length() * gram.length();
                        else
                            num += gram.length() * gram.length();
                    }
                }
            }
            if (num > 0) {
                // prio en
                if (lang.equals("en"))
                    num *= 1.2;

                list.add(new LangPrio(lang, num));
            }
        }
        Collections.sort(list, prioComparator);
        return list;
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

    public static List<String> gramOld(List<String> tokens) {
        int len = tokens.size();
        for (int index = 0; index < len; index++) {
            String text = tokens.get(index);
            for (int i = 2; i < text.length(); i++) {
                String str = text.substring(0, i);
                tokens.add(str);
            }
        }
        return tokens;
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

    Set<String> read(Reader tmpReader) throws IOException {
        BufferedReader reader = new BufferedReader(tmpReader);
        String line;
        try {
            Set<String> list = new HashSet<String>();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("//") || line.startsWith("#") || line.isEmpty())
                    continue;

                list.add(line.trim());
            }
            return list;
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
