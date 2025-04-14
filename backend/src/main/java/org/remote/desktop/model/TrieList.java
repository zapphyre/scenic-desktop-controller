package org.remote.desktop.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TrieList extends LinkedList<String> {
    List<String> elements;

    @Override
    public boolean equals(Object o) {

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elements);
    }
}
