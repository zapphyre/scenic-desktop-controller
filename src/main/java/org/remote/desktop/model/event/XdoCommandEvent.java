package org.remote.desktop.model.event;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.vto.SceneVto;
import org.springframework.context.ApplicationEvent;

@Value
public class XdoCommandEvent extends ApplicationEvent {
    EKeyEvt keyEvt;
    String keyPress;
    SceneVto nextScene;

    public XdoCommandEvent(Object source, EKeyEvt keyEvt, String keyPress, SceneVto nextScene) {
        super(source);
        this.keyEvt = keyEvt;
        this.keyPress = keyPress;
        this.nextScene = nextScene;
    }
}
