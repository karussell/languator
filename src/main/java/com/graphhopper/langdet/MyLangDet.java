package com.graphhopper.langdet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Peter Karich
 */
public class MyLangDet {

    private Set<String> detSet = new HashSet<String>();

    public void init() throws IOException {
        // detSet = read(new InputStreamReader(getClass().getResourceAsStream("lang_det_de.txt")));
        detSet = read(new InputStreamReader(getClass().getResourceAsStream("decompound_de.txt")));
    }

    public String getLang(String text) {
        for (String shortText : detSet) {
            if (text.toLowerCase().contains(shortText))
                return "de";
        }

        return "";
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

    public static List<String> gram(List<String> tokens) {
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

    Set<String> read(Reader tmpReader) throws IOException {
        BufferedReader reader = new BufferedReader(tmpReader);
        String line;
        try {
            Set<String> list = new HashSet<String>();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("//") || line.startsWith("#")) {
                    continue;
                }

                if (line == null || line.isEmpty())
                    continue;
                list.add(line.trim());
            }
            return list;
        } finally {
            reader.close();
        }
    }
}
