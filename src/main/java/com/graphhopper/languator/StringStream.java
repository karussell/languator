package com.graphhopper.languator;

/**
 * @author Peter Karich
 */
public interface StringStream {

    boolean hasNext();

    String next();
    
    int getCurrentSize();

    String getName();
}
