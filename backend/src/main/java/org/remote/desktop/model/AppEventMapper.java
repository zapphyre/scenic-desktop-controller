package org.remote.desktop.model;

import org.remote.desktop.model.dto.XdoActionDto;
import org.springframework.context.ApplicationEvent;

import java.util.function.Function;

public interface AppEventMapper {
    Function<XdoActionDto, ApplicationEvent> mapEvent(ButtonActionDef def, NextSceneXdoAction sceneXdoAction);
}
