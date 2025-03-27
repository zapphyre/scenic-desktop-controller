package org.remote.desktop.model;

public class NamedEnum extends LogicalName {

    public NamedEnum(Class<? extends Enum<?>> enumClass) {
        super(enumClass);
    }
}
