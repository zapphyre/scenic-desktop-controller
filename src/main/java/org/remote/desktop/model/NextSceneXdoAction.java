package org.remote.desktop.model;

import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.model.vto.XdoActionVto;

import java.util.List;

public record NextSceneXdoAction(SceneVto nextScene, List<XdoActionVto> actions) {
}
