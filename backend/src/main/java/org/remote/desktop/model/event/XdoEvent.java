package org.remote.desktop.model.event;

import lombok.Value;
import org.remote.desktop.pojo.KeyPart;
import org.springframework.context.ApplicationEvent;

@Value
public class XdoEvent extends ApplicationEvent {

    KeyPart keyPart;

    public XdoEvent(Object source, KeyPart keyPart) {
        super(source);
        this.keyPart = keyPart;
    }
}
