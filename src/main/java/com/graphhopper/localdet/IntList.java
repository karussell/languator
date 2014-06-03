package com.graphhopper.localdet;

import java.util.Arrays;

/**
 * @author Peter Karich
 */
public class IntList {

    private static final int[] EMPTY = new int[0];
    private int[] arr = EMPTY;

    public IntList() {
    }

    public IntList(int size) {
        arr = new int[size];
    }

    public void set(int index, int val) {
        if (index >= arr.length)
            arr = Arrays.copyOf(arr, index + 1);

        arr[index] = val;
    }

    public int get(int index) {
        return arr[index];
    }

    public int size() {
        return arr.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(arr);
    }
}
