package org.remote.desktop.model;

import java.util.Set;

public record NextSceneXdoAction(SceneVto nextScene, Set<XdoActionVto> actions) {
}
