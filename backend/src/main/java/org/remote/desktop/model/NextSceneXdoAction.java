package org.remote.desktop.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.dto.XdoActionDto;

import java.util.List;

@With
@Value
@Builder
public class NextSceneXdoAction {
    SceneDto nextScene;
    SceneDto eventSourceScene;
    List<XdoActionDto> actions;
    ButtonActionDef buttonTrigger;
}
