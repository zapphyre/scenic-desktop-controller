package org.remote.desktop.event;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.desktop.remote.mode.GpadOsActionModule;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.remote.desktop.model.event.ModeEvent;
import org.remote.desktop.service.impl.ModeService;
import org.remote.desktop.service.impl.StateService;
import org.remote.desktop.ui.select.mode.ModeSelector;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GpadActuatorRouter implements ApplicationListener<GpadCommandEvent> {

    private final ApplicationEventPublisher eventPublisher;
    private final ModeSelector  modeSelector;
    private final Map<String, GpadOsActionModule> actuatorModules;
    private final ModeService modeService;
    private final StateService stateService;

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

        if (e.getKeyPart().getKeyEvt().equals("MODE_SELECT"))
            modeSelector.getApplication().render(null, "");

        modeService.getCurrentMode().handleEvent(e.getKeyPart().getKeyEvt(), e.getKeyPart().getKeyStrokes());

//        ApplicationEvent evt = mode.currentModeEvent(e);
//        eventPublisher.publishEvent(evt);
    }

    @Component
    class ModeEventRouter implements ApplicationListener<ModeEvent> {

        @Override
        public void onApplicationEvent(ModeEvent event) {
            GpadOsActionModule module = actuatorModules.get(event.getMode());
            modeService.setCurrentMode(module);

            modeSelector.getApplication().close();
            stateService.nullifyForced();
        }
    }
}
