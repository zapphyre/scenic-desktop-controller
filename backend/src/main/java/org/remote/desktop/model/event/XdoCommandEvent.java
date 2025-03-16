package org.remote.desktop.model.event;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.pojo.KeyPart;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

@Value
public class XdoCommandEvent extends ApplicationEvent {
    KeyPart keyPart;

    @EqualsAndHashCode.Exclude
    SceneDto nextScene;

    public XdoCommandEvent(Object source, EKeyEvt keyEvt, String keyPress, SceneDto nextScene) {
        super(source);
        this.nextScene = nextScene;
        this.keyPart = new KeyPart(keyEvt, keyPress);
    }

    public XdoCommandEvent(KeyPart keyPart, Object source) {
        super(source);
        this.keyPart = keyPart;
        this.nextScene = null;
    }

}
