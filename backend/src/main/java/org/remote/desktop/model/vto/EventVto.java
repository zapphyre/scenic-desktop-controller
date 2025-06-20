package org.remote.desktop.model.vto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
@RequiredArgsConstructor
public class EventVto {

    Long id;

    GestureEventVto gestureEvent;
    ButtonEventVto buttonEvent;

    Long parentFk;
    Long nextSceneFk;

    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<XdoActionVto> actions;
}
