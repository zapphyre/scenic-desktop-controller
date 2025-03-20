package org.remote.desktop.pojo;

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
public class KeyPart {
    EKeyEvt keyEvt;
    List<String> keyStrokes;

    public KeyPart invert() {
        return KeyPart.builder()
                .keyStrokes(keyStrokes)
                .keyEvt(getKeyEvt() == EKeyEvt.PRESS ? EKeyEvt.RELEASE : EKeyEvt.PRESS)
                .build();
    }
}
