package org.remote.desktop.model;

import org.remote.desktop.model.dto.XdoActionDto;
import org.springframework.context.ApplicationEvent;

public interface AppEventMapper {
    ApplicationEvent mapEvent(ButtonActionDef def, NextSceneXdoAction sceneXdoAction, XdoActionDto xdoAction);
}
