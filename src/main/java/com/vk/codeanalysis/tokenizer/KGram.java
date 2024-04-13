package com.vk.codeanalysis.tokenizer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class KGram {

    protected final Queue<Integer> queue;
    protected final int k;
    protected final int xPowerK;
    protected final int x = 2;
    protected final int q = (1 << 31) - 1;
    protected int hashCode = 0;

    public KGram(int k) {
        queue = new ArrayDeque<>(k);
        this.k = k;
        xPowerK = (int) Math.pow(x, k);
    }

    public void put(int hash) {
        queue.add(hash);
        hashCode = (hashCode * x + hash) % q;
        if (queue.size() > k) {
            int last = queue.remove();
            hashCode = (hashCode - last * xPowerK) % q;
        }
    }

    public int getHashCode() {
        return hashCode;
    }
}
