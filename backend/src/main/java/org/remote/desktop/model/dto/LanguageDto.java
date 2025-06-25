package org.remote.desktop.model.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@ToString
@EqualsAndHashCode
public class LanguageDto {

    Long id;

    String code;
    String name;
    long size;

    byte[] trieDump;
}
