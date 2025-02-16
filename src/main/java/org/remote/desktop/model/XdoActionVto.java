package org.remote.desktop.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class XdoActionVto {
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Include
    @EqualsAndHashCode.Include
    private EKeyEvt keyEvt;

    @ToString.Include
    @EqualsAndHashCode.Include
    private String keyPress;

    private GPadEventVto gPadEvent;
}
