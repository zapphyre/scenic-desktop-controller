package org.remote.desktop.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.remote.desktop.model.EKeyEvt;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XdoActionDto {
    private Long id;
    private EKeyEvt keyEvt;
    private String keyPress;

//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    @JsonBackReference
//    private GPadEventVto gPadEvent;
}
