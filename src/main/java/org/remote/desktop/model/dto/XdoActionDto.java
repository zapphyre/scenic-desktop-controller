package org.remote.desktop.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.remote.desktop.model.EKeyEvt;

@Data
@Builder
//@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
public class XdoActionDto {
    long id;
    EKeyEvt keyEvt;
    String keyPress;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    GamepadEventDto gamepadEvent;
}
