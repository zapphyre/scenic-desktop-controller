package org.remote.desktop.util;

import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class BidirectionalSpliterator<T> implements Spliterator<T> {
    private final ListIterator<T> iterator;
    private final int size;
    private T currentElement; // Track the current element
    private boolean hasCurrent; // Track if currentElement is valid

    public BidirectionalSpliterator(List<T> list) {
        this.iterator = list.listIterator();
        this.size = list.size();
        this.currentElement = null;
        this.hasCurrent = false;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (hasCurrent |= iterator.hasNext()) {
            action.accept(currentElement = iterator.next());
            return true;
        }

        return false;
    }

    public boolean tryReverse(Consumer<? super T> action) {
        if (iterator.hasPrevious()) {
            currentElement = iterator.previous();
            hasCurrent = true;
            action.accept(currentElement);
            return true;
        }
        hasCurrent = false;
        return false;
    }

    // Get the current element without advancing
    public T getCurrent() {
        if (hasCurrent) {
            return currentElement;
        }
        // If no current element, try to get the first element without advancing
        if (iterator.hasNext()) {
            currentElement = iterator.next();
            iterator.previous(); // Move back to maintain position
            hasCurrent = true;
            return currentElement;
        }
        return null; // No elements available
    }

    @Override
    public Spliterator<T> trySplit() {
        return null; // Not splitting in this example
    }

    @Override
    public long estimateSize() {
        return size - iterator.nextIndex();
    }

    @Override
    public int characteristics() {
        return ORDERED | SIZED | SUBSIZED;
    }
}
