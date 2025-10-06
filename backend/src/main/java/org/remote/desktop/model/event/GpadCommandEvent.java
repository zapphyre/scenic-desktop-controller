package org.remote.desktop.model.event;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadDevice;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.pojo.KeyPart;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Set;

@Value
public class GpadCommandEvent extends ApplicationEvent {
    KeyPart keyPart;

    @EqualsAndHashCode.Exclude
    SceneDto nextScene;
    String recognizedSceneName;
    String trigger;
    String sourceSceneWindowName;
    Set<EButtonAxisMapping> modifiers;
    boolean longPress;
    GamepadDevice device;

    public GpadCommandEvent(Object source, String keyEvt, List<String> keyStrokes, SceneDto nextScene, String recognizedSceneName , String trigger, String sourceSceneWindowName, Set<EButtonAxisMapping> modifiers, boolean longPress, GamepadDevice device) {
        super(source);
        this.nextScene = nextScene;
        this.recognizedSceneName = recognizedSceneName;
        this.trigger = trigger;
        this.modifiers = modifiers;
        this.longPress = longPress;
        this.device = device;
        this.keyPart = new KeyPart(keyEvt, keyStrokes);
        this.sourceSceneWindowName = sourceSceneWindowName;
    }

    public GpadCommandEvent(KeyPart keyPart, Object source) {
        super(source);
        this.keyPart = keyPart;
        this.device = null;
        this.modifiers = Set.of();
        this.longPress = false;
        this.nextScene = null;
        this.trigger = null;
        this.sourceSceneWindowName = null;
        recognizedSceneName = "-";
    }

}
