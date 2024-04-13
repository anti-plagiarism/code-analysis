package com.vk.codeanalysis.tokenizer;

import java.util.Iterator;
import java.util.function.Function;

public class MapIterator<I, O> implements Iterator<O> {
    private final Iterator<I> input;
    private final Function<I, O> function;
    public MapIterator(Iterator<I> inputIterator, Function<I, O> function) {
        this.input = inputIterator;
        this.function = function;
    }
    @Override
    public boolean hasNext() {
        return input.hasNext();
    }

    @Override
    public O next() {
        return function.apply(input.next());
    }
}
