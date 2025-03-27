package org.remote.desktop.model;

import java.util.List;
import java.util.function.Function;

public class ExpandedLogicalNaming extends LogicalName {

    private final String prefix;
    public ExpandedLogicalNaming(Class<? extends Enum<?>> enumClass, String prefix) {
        super(enumClass);
        this.prefix = prefix;
    }

    @Override
    public List<String> names() {
        return super.names().stream()
                .map(withPrefix(prefix))
                .toList();
    }

    public static Function<String, String> withPrefix(String prefix) {
        return q -> "%S_%S".formatted(prefix, q);
    }
}
