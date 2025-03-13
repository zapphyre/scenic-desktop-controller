package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.remote.desktop.model.EKeyEvt;

@Value
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class XdoActionDto {
    Long id;
    EKeyEvt keyEvt;
    String keyPress;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    GamepadEventDto gamepadEvent;
}
