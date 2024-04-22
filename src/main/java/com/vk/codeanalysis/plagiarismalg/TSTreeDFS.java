package com.vk.codeanalysis.plagiarismalg;

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
        if (trace.peek().first.getChildCount() > trace.peek().second) {
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
        TSNode nowNode = trace.peek().first;
        int nowChild = trace.peek().second;
        trace.peek().second = nowChild + 1;
        trace.push(new Tuple<>(nowNode.getChild(nowChild), 0));
        return trace.peek().first;
    }
}
