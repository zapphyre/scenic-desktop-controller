package org.remote.desktop.component;

import java.util.Collection;
import java.util.HashSet;

public class ReplaceableSet<T> extends HashSet<T> {

    public ReplaceableSet() {
        super();
    }

    public ReplaceableSet(Collection<? extends T> c) {
        super(c);
    }

    public boolean replace(T t) {
        remove(t);
        return add(t);
    }

    public void replaceAll(Collection<? extends T> c) {
        clear();
        addAll(c);
    }
}
