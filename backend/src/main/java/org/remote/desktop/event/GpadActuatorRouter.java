package org.remote.desktop.event;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.mode.EventModeFactory;
import org.remote.desktop.mode.GpadOsModuleLoader;
import org.remote.desktop.mode.model.Mode;
import org.remote.desktop.mode.model.XdoMode;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.remote.desktop.model.event.ModeEvent;
import org.remote.desktop.mode.modul.GpadOsActionModule;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Import(GpadOsModuleLoader.class)
@RequiredArgsConstructor
public class GpadActuatorRouter implements ApplicationListener<GpadCommandEvent> {

    private final ApplicationEventPublisher eventPublisher;

    private final Map<String, GpadOsActionModule> actuatorModules;

    private  GpadOsActionModule currentMode;

//    private final EventModeFactory modeFactory = new EventModeFactory(new XdoMode());
//
//    private Mode mode = modeFactory.getLastMode();

    @PostConstruct
    void init() {
        currentMode = actuatorModules.get("DESKTOP");
    }

    @Override
    public void onApplicationEvent(GpadCommandEvent e) {
//        mode = switch (e.getKeyPart().getKeyEvt()) {
//            case UI_ANALOG_ADJUST -> modeFactory.changeMode(EMode.TRIGGER_SELECT);
//            case KEYBOARD_ON -> modeFactory.changeMode(EMode.KEYBOARD);
//            case WINDER -> modeFactory.changeMode(EMode.WINDER);
//
//            case SCENE_RESET -> modeFactory.changeMode(EMode.XDO);
//
//            default -> modeFactory.getLastMode();
//        };

        currentMode.handleEvent(e.getKeyPart().getKeyEvt(), e.getKeyPart().getKeyStrokes());

//        ApplicationEvent evt = mode.currentModeEvent(e);
//        eventPublisher.publishEvent(evt);
    }

    @Component
    class ModeEventRouter implements ApplicationListener<ModeEvent> {

        @Override
        public void onApplicationEvent(ModeEvent event) {
            currentMode = actuatorModules.get(event.getMode());
        }
    }
}
