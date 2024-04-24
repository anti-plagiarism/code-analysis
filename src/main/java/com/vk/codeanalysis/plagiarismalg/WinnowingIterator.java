package com.vk.codeanalysis.plagiarismalg;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WinnowingIterator implements Iterator<Integer> {
    private final Iterator<Integer> iterator;
    private final int[] window;
    private final int k;
    private boolean filled = false;
    private int counter;
    private int minValue;

    public WinnowingIterator(Iterator<Integer> iterator, int k) {
        this.iterator = iterator;
        this.k = k;
        this.window = new int[k];
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Integer next() {
        int ans;
        while (true) {
            int prevValue = window[counter];
            int newValue = iterator.next();
            window[counter] = newValue;
            counter = (counter + 1) % window.length;
            if (counter == 0) {
                filled = true;
            }
            if (!filled) {
                if (!iterator.hasNext()) {
                    return newValue;
                }
                continue;
            }
            if (prevValue == minValue) {
                minValue = Integer.MAX_VALUE;
                for (int j : window) {
                    minValue = Math.min(minValue, j);
                }
                return minValue;
            }
            if (newValue < minValue) {
                minValue = newValue;
                ans = newValue;
                break;
            }
            if (!iterator.hasNext()) {
                return newValue;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(77, 74, 42, 17, 98, 50, 17, 98, 8, 88, 67, 39, 77, 74, 42, 17, 98);
        Iterator<Integer> iterator = new WinnowingIterator(list.iterator(), 5);
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
