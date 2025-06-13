package org.remote.desktop.model.dto.rest;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class NewGestureRequestDto {

    long id;

    @Builder.Default
    EStick stick = EStick.LEFT;
}
