package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.mode.EventModeFactory;
import org.remote.desktop.mode.model.Mode;
import org.remote.desktop.mode.model.XdoMode;
import org.remote.desktop.model.EAdapterMode;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.remote.desktop.model.event.ModeEvent;
import org.remote.desktop.model.modul.GpadOsActionModule;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GpadActuatorRouter implements ApplicationListener<GpadCommandEvent> {

    private final ApplicationEventPublisher eventPublisher;
    private final EventModeFactory modeFactory = new EventModeFactory(new XdoMode());

    private Mode mode = modeFactory.getLastMode();

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

        ApplicationEvent evt = mode.currentModeEvent(e);
        eventPublisher.publishEvent(evt);
    }

    @Component
    class ModeEventRouter implements ApplicationListener<ModeEvent> {



        @Override
        public void onApplicationEvent(ModeEvent event) {
            GpadOsActionModule<EAdapterMode> modeGpadOsActionModule;

            modeGpadOsActionModule.


            mode = modeFactory.changeMode(event.getMode());
        }
    }
}
