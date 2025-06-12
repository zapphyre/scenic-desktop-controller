package org.remote.desktop.model.dto.rest;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class NewGestureRequestDto {

    long id;
    EStick stick;
}
