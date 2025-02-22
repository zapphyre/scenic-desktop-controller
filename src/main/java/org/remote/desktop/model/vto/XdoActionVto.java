package org.remote.desktop.model.vto;

import lombok.*;
import org.remote.desktop.model.EKeyEvt;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XdoActionVto {
    private Long id;
    private EKeyEvt keyEvt;
    private String keyPress;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GPadEventVto gPadEvent;
}
