package org.remote.desktop.model.vto;

import lombok.Builder;
import lombok.Value;
import org.remote.desktop.model.EKeyEvt;

@Value
@Builder
public class XdoActionVto {
    Long id;
    EKeyEvt keyEvt;
    String keyPress;

    Long gamepadEventFk;
}
