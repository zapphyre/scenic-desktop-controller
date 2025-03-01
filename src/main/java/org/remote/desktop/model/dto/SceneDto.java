package org.remote.desktop.model.dto;

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
public class SceneDto {

    @ToString.Include
    @EqualsAndHashCode.Include
    private String name;
    private String windowName;

    private SceneDto inherits;

    @Builder.Default
    private EAxisEvent leftAxisEvent = EAxisEvent.NOOP;
    @Builder.Default
    private EAxisEvent rightAxisEvent = EAxisEvent.NOOP;

    @Builder.Default
    private List<GPadEventDto> gPadEvents = new LinkedList<>();
}
