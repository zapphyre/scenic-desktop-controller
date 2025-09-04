package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.remote.desktop.model.EKeyEvt;

import java.util.List;

@Value
@Builder
@Jacksonized
@RequiredArgsConstructor
public class XdoActionVto {
    Long id;
    EKeyEvt keyEvt;

    List<String> keyStrokes;

    Long eventFk;

    String activator;
}
