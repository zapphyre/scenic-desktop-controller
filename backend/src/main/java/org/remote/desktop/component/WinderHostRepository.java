package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.config.FeignBuilder;
import org.remote.desktop.db.dao.EventDao;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.EAdapterMode;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.event.WinderCommandEvent;
import org.remote.desktop.model.vto.EventVto;
import org.remote.desktop.model.vto.SceneVto;
import org.remote.desktop.model.vto.XdoActionVto;
import org.remote.desktop.service.impl.StateService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.winder.api.WinderConstants;
import org.winder.api.WinderNativeConnectorApi;
import org.winder.common.model.EWinderCommand;
import org.winder.common.model.EWinderOp;
import org.zapphyre.discovery.intf.JmAutoRegistry;
import org.zapphyre.discovery.model.JmDnsProperties;
import org.zapphyre.discovery.model.WebSourceDef;

import java.util.*;

import static org.remote.desktop.db.dao.SettingsDao.WINDER_SCENE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class WinderHostRepository implements JmAutoRegistry, ApplicationListener<WinderCommandEvent> {

    private final SceneDao sceneDao;
    private final SettingsDao settingsDao;
    private final FeignBuilder feignBuilder;
    private final EventDao eventDao;
    private final StateService stateService;
    private WinderNativeConnectorApi winderApi;

    public SceneVto getWinderScenery() {
        return Optional.ofNullable(sceneDao.getSceneVtoBy(WINDER_SCENE_NAME))
                .orElseGet(() -> sceneDao.create(createPresetWinderScene(Arrays.asList(EWinderOp.values()))));
    }

    @PostConstruct
    void initScenery() {
        SceneVto scenery = getWinderScenery();

        List<EWinderOp> allOps = new ArrayList<>(Arrays.asList(EWinderOp.values()));

        scenery.getEvents().stream()
                .flatMap(q -> q.getActions().stream())
                .filter(q -> q.getMode() == EAdapterMode.WINDER)
                .flatMap(event -> event.getKeyStrokes().stream())
                .map(EWinderOp::valueOf)
                .forEach(allOps::remove);

        if (allOps.isEmpty()) return;

        buildEventsFrom(allOps).stream()
                .map(q -> q.withParentFk(scenery.getId()))
                .forEach(eventDao::create);
    }

    public Map<EWinderOp, EventVto> getOpEventMap() {
        Map<EWinderOp, EventVto> opEventMap = new HashMap<>();

        for (EventVto event : getWinderScenery().getEvents())
            try {
                opEventMap.put(EWinderOp.valueOf(event.getActions().getFirst().getKeyStrokes().getFirst()), event);
            } catch (Exception e) {
                System.out.println("bad value for EOp enum" + event.getActions().getFirst().getKeyStrokes().getFirst());
            }

        return opEventMap;
    }

    SceneVto createPresetWinderScene(List<EWinderOp> ops) {
        return SceneVto.builder()
                .name(WINDER_SCENE_NAME)
                .windowName(WINDER_SCENE_NAME)
                .rightAxisEvent(EAxisEvent.NOOP)
                .leftAxisEvent(EAxisEvent.NOOP)
                .events(buildEventsFrom(ops))
                .build();
    }

    List<EventVto> buildEventsFrom(List<EWinderOp> ops) {
        return ops.stream()
                .map(q -> EventVto.builder()
                        .actions(List.of(XdoActionVto.builder()
                                .mode(EAdapterMode.WINDER)
                                .keyEvt(EKeyEvt.STROKE)
                                .keyStrokes(List.of(q.name()))
                                .build())
                        ).build()
                )
                .toList();
    }

    public void sourceDiscovered(WebSourceDef webSourceDef) {
        System.out.println("WINDER sourceDiscovered: " + webSourceDef);
        winderApi = feignBuilder.buildWinderNativeConnectorApiClient(webSourceDef.getBaseUrl());
    }

    public void sourceLost(WebSourceDef s) {
        winderApi = null;
        log.warn("disconnected from winder: " + s.getName());
    }

    public JmDnsProperties getJmDnsProperties() {
        String winderInstanceName = settingsDao.getSettings().getWinderInstanceName();
        return JmDnsProperties.builder()
                .greetingMessage("hi")
                .group(WinderConstants.JM_GROUP)
                .instanceName(winderInstanceName)
                .build();
    }

    @Override
    public void onApplicationEvent(WinderCommandEvent event) {
        log.info("winder event: " + event);

        if (event.getWinderOp().equals(EWinderOp.EX) || Objects.isNull(winderApi))
            stateService.nullifyForced();
        else
            winderApi.command(EWinderCommand.builder()
                    .operation(event.getWinderOp())
                    .build());
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
