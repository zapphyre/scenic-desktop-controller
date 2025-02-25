package org.remote.desktop.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.model.vto.XdoActionVto;

import java.util.List;

@With
@Value
@Builder
public class NextSceneXdoAction {
    SceneVto nextScene;
    List<XdoActionVto> actions;
    ButtonActionDef buttonTrigger;
}
