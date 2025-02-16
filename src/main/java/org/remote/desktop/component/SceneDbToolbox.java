package org.remote.desktop.component;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.mapper.ActionMapper;
import org.remote.desktop.mapper.CycleAvoidingMappingContext;
import org.remote.desktop.mapper.SceneMapper;
import org.remote.desktop.mapper.XdoActionMapper;
import org.remote.desktop.model.ActionVto;
import org.remote.desktop.model.SceneVto;
import org.remote.desktop.model.XdoActionVto;
import org.remote.desktop.repository.ActionRepository;
import org.remote.desktop.repository.SceneRepository;
import org.remote.desktop.repository.XdoActionRepository;
import org.remote.desktop.ui.view.component.SaveNotifiaction;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SceneDbToolbox {

    private final SceneRepository sceneRepository;
    private final ActionRepository actionRepository;
    private final XdoActionRepository xdoActionRepository;

    private final SceneMapper sceneMapper;
    private final XdoActionMapper xdoActionMapper;
    private final ActionMapper actionMapper;


    public XdoActionVto save(XdoActionVto actionVto) {
        XdoActionVto xdoActionVto = null;
        try {
            xdoActionVto = Optional.of(actionVto)
                    .map(q -> xdoActionMapper.map(q, new CycleAvoidingMappingContext()))
                    .map(xdoActionRepository::save)
                    .map(q -> xdoActionMapper.map(q, new CycleAvoidingMappingContext()))
                    .orElseThrow();
            SaveNotifiaction.success("saved");
        } catch (Exception e) {
            e.printStackTrace();
            SaveNotifiaction.error();
        }

        return xdoActionVto;
    }

    public void update(XdoActionVto actionVto) {
        try {
            Optional.of(actionVto)
                    .map(q -> xdoActionMapper.map(q, new CycleAvoidingMappingContext()))
                    .map(xdoActionRepository::save)
                    .ifPresent(q -> xdoActionMapper.update(q, actionVto, new CycleAvoidingMappingContext()));
            SaveNotifiaction.success("updated");
        } catch (Exception e) {
            e.printStackTrace();
            SaveNotifiaction.error();
        }
    }

    public void remove(XdoActionVto vto) {
        try {
            Optional.of(vto)
                    .map(q -> xdoActionMapper.map(q, new CycleAvoidingMappingContext()))
                    .ifPresent(xdoActionRepository::delete);
            SaveNotifiaction.success("removed");
        } catch (Exception e) {
            e.printStackTrace();
            SaveNotifiaction.error();
        }
    }

    public void update(ActionVto actionVto) {
        try {
            Optional.of(actionVto)
                    .map(q -> actionMapper.map(q, new CycleAvoidingMappingContext()))
                    .map(actionRepository::save)
                    .ifPresent(q -> actionMapper.update(q, actionVto, new CycleAvoidingMappingContext()));
            SaveNotifiaction.success("updated");
        } catch (Exception e) {
            e.printStackTrace();
            SaveNotifiaction.error();
        }
    }

    public void remove(ActionVto vto) {
        try {
            Optional.of(vto)
                    .map(q -> actionMapper.map(q, new CycleAvoidingMappingContext()))
                    .ifPresent(actionRepository::delete);
            SaveNotifiaction.success("removed");
        } catch (Exception e) {
            e.printStackTrace();
            SaveNotifiaction.error();
        }
    }

    public void update(SceneVto sceneVto) {
        try {
            Optional.of(sceneVto)
                    .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                    .map(sceneRepository::save)
                    .ifPresent(q -> sceneMapper.update(q, sceneVto, new CycleAvoidingMappingContext()));
            SaveNotifiaction.success("updated");
        } catch (Exception e) {
            e.printStackTrace();
            SaveNotifiaction.error();
        }
    }

    public void remove(SceneVto vto) {
        try {
            Optional.of(vto)
                    .map(q -> sceneMapper.map(q, new CycleAvoidingMappingContext()))
                    .ifPresent(sceneRepository::delete);
            SaveNotifiaction.success("removed");
        } catch (Exception e) {
            e.printStackTrace();
            SaveNotifiaction.error();
        }
    }
}
