package org.remote.desktop.model.event;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.model.EAdapterMode;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.pojo.KeyPart;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Set;

@Value
public class GpadCommandEvent extends ApplicationEvent {
    KeyPart keyPart;

    EAdapterMode mode;
    @EqualsAndHashCode.Exclude
    SceneDto nextScene;
    String trigger;
    String sourceSceneWindowName;
    Set<EButtonAxisMapping> modifiers;
    boolean longPress;

    public GpadCommandEvent(Object source, EKeyEvt keyEvt, List<String> keyStrokes, EAdapterMode mode, SceneDto nextScene, String trigger, String sourceSceneWindowName, Set<EButtonAxisMapping> modifiers, boolean longPress) {
        super(source);
        this.mode = mode;
        this.nextScene = nextScene;
        this.trigger = trigger;
        this.modifiers = modifiers;
        this.longPress = longPress;
        this.keyPart = new KeyPart(keyEvt, keyStrokes);
        this.sourceSceneWindowName = sourceSceneWindowName;
    }

    public GpadCommandEvent(KeyPart keyPart, Object source) {
        super(source);
        this.keyPart = keyPart;
        this.modifiers = Set.of();
        this.longPress = false;
        this.nextScene = null;
        this.trigger = null;
        this.sourceSceneWindowName = null;
        this.mode = EAdapterMode.DESKTOP;
    }

}
