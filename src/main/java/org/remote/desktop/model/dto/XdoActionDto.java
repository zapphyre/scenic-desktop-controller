package org.remote.desktop.model.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.remote.desktop.model.EKeyEvt;

@Value
@Builder
public class XdoActionDto {
    Long id;
    EKeyEvt keyEvt;
    String keyPress;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    GamepadEventDto gamepadEvent;
}
