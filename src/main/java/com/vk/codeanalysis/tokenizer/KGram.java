package com.vk.codeanalysis.tokenizer;

import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Queue;

public class KGram {
    protected final Queue<Integer> queue;
    protected final int k;
    protected final int xPowerK;
    protected final int x = 2;
    protected final int q = (1 << 31) - 1;

    @Getter
    protected int hashCode;

    public KGram(int k) {
        this.queue = new ArrayDeque<>(k);
        this.k = k;
        this.xPowerK = (int) Math.pow(x, k);
    }

    public void put(int hash) {
        queue.add(hash);
        hashCode = (hashCode * x + hash) % q;
        if (queue.size() > k) {
            int last = queue.remove();
            hashCode = (hashCode - last * xPowerK) % q;
        }
    }
}
