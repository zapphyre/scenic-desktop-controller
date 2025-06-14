package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class GestureEventVto {

    Long id;

    Long leftStickGestureFk;
    Long rightStickGestureFk;
}
