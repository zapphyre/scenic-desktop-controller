package org.remote.desktop.model.vto;

import lombok.*;
import org.remote.desktop.model.EAxisEvent;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SceneVto {

    @ToString.Include
    @EqualsAndHashCode.Include
    private String name;
    private String windowName;
    private SceneVto inherits;
    @Builder.Default
    private EAxisEvent leftAxisEvent = EAxisEvent.NOOP;
    @Builder.Default
    private EAxisEvent rightAxisEvent = EAxisEvent.NOOP;

    @Builder.Default
    private List<GPadEventVto> gPadEvents = new LinkedList<>();
}
