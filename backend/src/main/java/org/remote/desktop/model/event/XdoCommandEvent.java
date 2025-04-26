package org.remote.desktop.model.event;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.pojo.KeyPart;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Set;

@Value
public class XdoCommandEvent extends ApplicationEvent {
    KeyPart keyPart;

    @EqualsAndHashCode.Exclude
    SceneDto nextScene;
    String trigger;
    String sourceSceneWindowName;
    Set<EButtonAxisMapping> modifiers;
    boolean longPress;

    public XdoCommandEvent(Object source, EKeyEvt keyEvt, List<String> keyStrokes, SceneDto nextScene, String trigger, String sourceSceneWindowName, Set<EButtonAxisMapping> modifiers, boolean longPress) {
        super(source);
        this.nextScene = nextScene;
        this.trigger = trigger;
        this.modifiers = modifiers;
        this.longPress = longPress;
        this.keyPart = new KeyPart(keyEvt, keyStrokes);
        this.sourceSceneWindowName = sourceSceneWindowName;
    }

    public XdoCommandEvent(KeyPart keyPart, Object source) {
        super(source);
        this.keyPart = keyPart;
        this.modifiers = Set.of();
        this.longPress = false;
        this.nextScene = null;
        this.trigger = null;
        this.sourceSceneWindowName = null;
    }

}
