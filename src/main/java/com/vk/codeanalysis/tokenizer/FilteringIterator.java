package com.vk.codeanalysis.tokenizer;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class FilteringIterator<I> implements Iterator<I> {
    private final Iterator<I> iterator;
    private final Function<I, Boolean> filter;
    private I next;

    public FilteringIterator(Iterator<I> iterator, Function<I, Boolean> filter) {
        this.iterator = iterator;
        this.filter = filter;
    }

    @Override
    public boolean hasNext() {
        if (next != null) {
            return true;
        }
        findNext();
        return next != null;
    }

    @Override
    public I next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No next element");
        }
        I ans = next;
        next = null;
        return ans;
    }


    private void findNext() {
        while (iterator.hasNext()) {
            I next = iterator.next();
            if (filter.apply(next)) {
                this.next = next;
                return;
            }
        }
        this.next = null;
    }

}
