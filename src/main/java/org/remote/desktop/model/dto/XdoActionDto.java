package org.remote.desktop.model.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.remote.desktop.model.EKeyEvt;

@Value
@Builder
@Jacksonized
public class XdoActionDto {
    Long id;
    EKeyEvt keyEvt;
    String keyPress;

//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    @JsonBackReference
//    private GPadEventVto gPadEvent;
}
