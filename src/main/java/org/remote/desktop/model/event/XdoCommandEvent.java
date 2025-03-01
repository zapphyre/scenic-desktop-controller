package org.remote.desktop.model.event;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.pojo.KeyPart;
import org.springframework.context.ApplicationEvent;

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

    public XdoCommandEvent invert() {
        EKeyEvt invertedEvt = keyPart.getKeyEvt() == EKeyEvt.PRESS ? EKeyEvt.RELEASE : EKeyEvt.PRESS;

        return new XdoCommandEvent(getSource(), invertedEvt, keyPart.getKeyPress(), nextScene);
    }

}
