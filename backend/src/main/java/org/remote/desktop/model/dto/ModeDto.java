package org.remote.desktop.model.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@RequiredArgsConstructor
public class ModeDto {

    Long id;

    String adapterMode;

    List<String> keyEvtTypes;

    List<String> nouns;

    Boolean scenic;
}
