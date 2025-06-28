package org.remote.desktop.model.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.With;

@With
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
