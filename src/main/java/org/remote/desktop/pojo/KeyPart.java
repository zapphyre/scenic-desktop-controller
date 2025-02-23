package org.remote.desktop.pojo;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.remote.desktop.model.EKeyEvt;

@Value
@Builder
@RequiredArgsConstructor
public class KeyPart {
    EKeyEvt keyEvt;
    String keyPress;
}
