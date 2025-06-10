package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class EventVto {

    Long id;

    GestureEventVto gestureEvent;
    ButtonEventVto buttonEvent;

    Long parentFk;
    Long nextSceneFk;

    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<XdoActionVto> actions = List.of();
}
