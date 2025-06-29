package org.remote.desktop.model.dto;

import lombok.*;

@With
@Value
@Builder
@ToString
@EqualsAndHashCode
public class LanguageDto {

    Long id;

    String code;
    String name;
    long size;

    byte[] trieDump;
}
