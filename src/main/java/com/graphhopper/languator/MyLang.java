package com.graphhopper.languator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Peter Karich
 */
public class MyLang {

    private final Map<String, Integer> parts = new HashMap<String, Integer>();
    private final String name;
    private int minLength = Integer.MAX_VALUE;
    private int maxLength = 0;

    public MyLang(String name) {
        this.name = name;
    }

    public void add(String wordPart, int freq) {
        if (wordPart.length() < minLength)
            minLength = wordPart.length();
        if (wordPart.length() > maxLength)
            maxLength = wordPart.length();

        Integer old = parts.put(wordPart, freq);
        if (old != null)
            throw new IllegalStateException("already set wordpart " + old);
    }

    public int getMaxLength() {
        return maxLength;
    }

    public int getMinLength() {
        return minLength;
    }

    public Collection<Entry<String, Integer>> getEntries() {
        return parts.entrySet();
    }

    public Integer get(String wordPart) {
        return parts.get(wordPart);
    }

    @Override
    public String toString() {
        return name;
    }

    public static MyLang build(String name, Map<String, Integer> map, int freq) {
        MyLang lang = new MyLang(name);
        for (Entry<String, Integer> e : map.entrySet()) {
            if (e.getValue() >= freq)
                lang.add(e.getKey(), e.getValue());
        }
        return lang;
    }

    void store(String name) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(name));
            try {
                for (Entry<String, Integer> e : parts.entrySet()) {
                    writer.append(normalize(e.getKey()));
                    writer.append(",");
                    writer.append(Integer.toString(e.getValue()));
                    writer.append("\n");
                }
                writer.flush();
            } finally {
                writer.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    static String normalize(String token) {
        // * query has usually insensitive case
        // * removing \\\' produces slightly smaller precision (tested for en,de,it and fr)
        return token.toLowerCase().replaceAll("[\\\"\\/\\:\\;\\&\\.\\!\\?\\)\\(\\[\\]\\,\\>\\<\\-\\n\\t\\&]", " ");
    }
}
