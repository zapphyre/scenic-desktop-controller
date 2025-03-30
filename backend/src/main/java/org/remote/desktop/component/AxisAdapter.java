package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.builder.closure.button.RawArrowSource;
import org.asmus.model.GamepadEvent;
import org.asmus.model.PolarCoords;
import org.remote.desktop.actuate.MouseCtrl;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.event.SceneStateRepository;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.*;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.dto.XdoActionDto;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class AxisAdapter {

    private final SceneDao sceneDao;
    private final SettingsDao settingsDao;
    private final SceneStateRepository sceneStateRepository;
    private final IntrospectedEventFactory gamepadObserver;
    private final TriggerActionMatcher triggerActionMatcher;
    private final ButtonPressMapper buttonPressMapper;
    private final CacheManager cacheManager;

    private Map<ELogicalTrigger, Node> nodeMap, original;

    //    private Consumer<PolarCoords> leftStickConsumer = MouseCtrl::moveMouse;
    private Consumer<PolarCoords> leftStickConsumer = (q) -> {
    };
    //    private Consumer<PolarCoords> rightStickConsumer = MouseCtrl::scroll;
    private Consumer<PolarCoords> rightStickConsumer = (q) -> {
    };

    private List<Gesture> gestures = List.of(
            Gesture.builder()
                    .triggers(List.of(ELogicalTrigger.LEFTX_DOWN, ELogicalTrigger.LEFTX_RIGHT, ELogicalTrigger.LEFTX_UP, ELogicalTrigger.LEFTX_LEFT))
                    .actions(List.of(XdoActionDto.builder()
                            .keyEvt(EKeyEvt.STROKE)
                            .keyStrokes(List.of("qwer"))
                            .build()))
                    .build(),
            Gesture.builder()
                    .triggers(List.of(ELogicalTrigger.LEFTX_LEFT, ELogicalTrigger.LEFTX_CENTER, ELogicalTrigger.LEFTX_LEFT))
                    .actions(List.of(XdoActionDto.builder()
                            .keyStrokes(List.of("back"))
                            .build()))
                    .build(),
//            Gesture.builder()
//                    .triggers(List.of(ELogicalTrigger.LEFTX_RIGHT, ELogicalTrigger.LEFTX_CENTER, ELogicalTrigger.LEFTX_RIGHT))
//                    .actions(List.of(XdoActionDto.builder()
//                            .keyStrokes(List.of("forward"))
//                            .build()))
//                    .build(),
            Gesture.builder()
                    .triggers(List.of(ELogicalTrigger.LEFTX_RIGHT, ELogicalTrigger.RIGHTX_RIGHT))
                    .actions(List.of(XdoActionDto.builder()
                            .keyStrokes(List.of("simulation crash"))
                            .build()))
                    .build()
    );

    public static Node buildGraph(List<ELogicalTrigger> triggers, List<XdoActionDto> actions) {
        Iterator<ELogicalTrigger> iterator = triggers.iterator();
        Node root, current = root = new Node(iterator.next());

        while (iterator.hasNext()) {
            Node next = new Node(iterator.next());
            current.addConnection(next.getTrigger(), next);
            current = next;
        }

        current.setActions(actions);

        return root;
    }


    private final Map<EAxisEvent, Consumer<PolarCoords>> axisEventMap = Map.of(
            EAxisEvent.MOUSE, MouseCtrl::moveMouse,
            EAxisEvent.SCROLL, MouseCtrl::scroll,
            EAxisEvent.VOL, e -> {
            },
            EAxisEvent.NOOP, e -> {
            }
    );


    @PostConstruct
    void init() {
//        updateAxisConsumers(settingsDao.getSettings().getBaseSceneName());
//        sceneStateRepository.registerRecognizedSceneObserver(this::updateAxisConsumers);

        nodeMap = original = gestures.stream()
                .map(q -> buildGraph(q.getTriggers(), q.getActions()))
                .collect(toMap(Node::getTrigger, Function.identity()));

        Flux<GamepadEvent> digitized = gamepadObserver.getButtonEventStream()
                .filter(q -> q.getType().ordinal() > 14)
                .distinctUntilChanged();

        digitized
                .window(Duration.ofMillis(210))
                .flatMap(Flux::collectList)
                .filter(List::isEmpty)
                .subscribe(q -> nodeMap = original);

        digitized.map(buttonPressMapper::map)
                .mapNotNull(ButtonActionDef::getLogicalTrigger)
                .mapNotNull(q -> nodeMap.get(q))
                .doOnNext(q -> nodeMap = q.getConnections())
                .mapNotNull(Node::getActions)
                .flatMap(Flux::fromIterable)
                .subscribe(q -> System.out.println("apply action: " + q));
    }

    public RawArrowSource getLeftStickProcessor() {
        return gamepadObserver.leftStickStream();
    }

    public RawArrowSource getRightStickProcessor() {
        return gamepadObserver.rightStickStream();
    }

    void updateAxisConsumers(String windowName) {
        Consumers consumers = cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME).get(windowName, Consumers.class);

        if (Objects.isNull(consumers)) {
            EAxisEvent leftAxisEvent = sceneDao.getSceneForWindowNameOrBase(windowName).getLeftAxisEvent();
            EAxisEvent rightAxisEvent = sceneDao.getSceneForWindowNameOrBase(windowName).getRightAxisEvent();

            SceneDto base = sceneDao.getScene(settingsDao.getSettings().getBaseSceneName());

            leftAxisEvent = leftAxisEvent == EAxisEvent.DEFAULT ? base.getLeftAxisEvent() : leftAxisEvent;
            rightAxisEvent = rightAxisEvent == EAxisEvent.DEFAULT ? base.getRightAxisEvent() : rightAxisEvent;

            leftStickConsumer = axisEventMap.get(leftAxisEvent);
            rightStickConsumer = axisEventMap.get(rightAxisEvent);

            cacheManager.getCache(SceneDao.SCENE_AXIS_CACHE_NAME)
                    .put(windowName, new Consumers(rightStickConsumer, leftStickConsumer));
        } else {
            leftStickConsumer = consumers.leftStickConsumer;
            rightStickConsumer = consumers.rightStickConsumer;
        }
    }

    public void getRightStickConsumer(PolarCoords pos) {
        rightStickConsumer.accept(pos);
    }

    public void getLeftStickConsumer(PolarCoords pos) {
        leftStickConsumer.accept(pos);
    }

    record Consumers(Consumer<PolarCoords> rightStickConsumer, Consumer<PolarCoords> leftStickConsumer) {
    }
}
