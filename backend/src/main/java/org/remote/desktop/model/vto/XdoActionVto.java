package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.remote.desktop.model.EKeyEvt;

@Value
@Builder
@Jacksonized
public class XdoActionVto {
    Long id;
    EKeyEvt keyEvt;
    String keyPress;

    Long gamepadEventFk;
}
