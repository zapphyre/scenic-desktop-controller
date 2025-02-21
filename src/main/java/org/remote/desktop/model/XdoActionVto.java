package org.remote.desktop.model;

import lombok.*;

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
