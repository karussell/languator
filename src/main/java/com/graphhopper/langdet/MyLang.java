package com.graphhopper.langdet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Peter Karich
 */
public class MyLang {

    private final Set<String> parts = new HashSet<String>();
    private final String name;
    private int minLength = Integer.MAX_VALUE;
    private int maxLength = 0;

    public MyLang(String name) {
        this.name = name;
    }

    public void add(String wordPart) {
        if (wordPart.length() < minLength)
            minLength = wordPart.length();
        if (wordPart.length() > maxLength)
            maxLength = wordPart.length();

        parts.add(wordPart);
    }

    public void addAll(Collection<String> wordParts) {
        for (String wordPart : wordParts) {
            add(wordPart);
        }
    }

    public int getMaxLength() {
        return maxLength;
    }

    public int getMinLength() {
        return minLength;
    }

    public Set<String> getParts() {
        return parts;
    }

    public boolean contains(String gram) {
        return parts.contains(gram);
    }
}
