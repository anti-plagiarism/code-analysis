package com.vk.codeanalysis.tokenizer;

import org.treesitter.TSNode;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class TSTreeDFS implements Iterator<TSNode> {
    private final Stack<Tuple<TSNode, Integer>> trace = new Stack<>();

    public TSTreeDFS(TSNode root) {
        trace.push(new Tuple<>(root, 0));
    }

    @Override
    public boolean hasNext() {
        if (trace.isEmpty()) {
            return false;
        }
        if (trace.peek().getFirst().getChildCount() > trace.peek().getSecond()) {
            return true;
        }
        trace.pop();
        return hasNext();
    }

    @Override
    public TSNode next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        TSNode nowNode = trace.peek().getFirst();
        int nowChild = trace.peek().getSecond();
        trace.peek().setSecond(nowChild + 1);
        trace.push(new Tuple<>(nowNode.getChild(nowChild), 0));
        return trace.peek().getFirst();
    }
}
