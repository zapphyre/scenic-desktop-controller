package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.remote.desktop.model.EKeyEvt;

@Data
@Builder
//@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
public class XdoActionDto {
    Long id;
    EKeyEvt keyEvt;
    String keyPress;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    GamepadEventDto gamepadEvent;
}
