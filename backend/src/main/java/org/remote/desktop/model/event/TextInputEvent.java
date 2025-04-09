package org.remote.desktop.model.event;

import lombok.Builder;
import lombok.Value;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.pojo.KeyPart;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Value
public class TextInputEvent extends ApplicationEvent {

    KeyPart keyPart;

    public TextInputEvent(Object source, EKeyEvt keyEvt, List<String> keyStrokes) {
        super(source);
        this.keyPart = new KeyPart(keyEvt, keyStrokes);
    }
}
