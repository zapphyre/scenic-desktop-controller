package org.remote.desktop.model;

import java.util.List;

public record NextSceneXdoAction(SceneVto nextScene, List<XdoActionVto> actions) {
}
