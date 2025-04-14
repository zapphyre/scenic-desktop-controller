package org.remote.desktop.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;
import java.util.Objects;

@Value
@Builder
public class TrieGroupDef {
    int group;
    String code;
    List<String> elements;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TrieGroupDef that = (TrieGroupDef) o;

        return elements.contains(that.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(elements);
    }
}
