package org.remote.desktop.model;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public abstract class LogicalName {

    protected final Class<? extends Enum<?>> enumClass;

    public List<String> names() {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .toList();
    }
}
