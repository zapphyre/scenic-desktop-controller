package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class LanguageVto {

    Long id;

    String name;
    String code;
    long size;
}
