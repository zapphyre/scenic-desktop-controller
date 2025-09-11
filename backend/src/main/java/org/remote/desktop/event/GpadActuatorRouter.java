package org.remote.desktop.event;

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
    private final ModeSelector modeSelector;
    private final Map<String, GpadOsActionModule> actuatorModules;
    private final ModeService modeService;

    @Override
    public void onApplicationEvent(GpadCommandEvent e) {
        if (e.getKeyPart().getKeyEvt().equals("MODE_SELECT"))
            if (e.getKeyPart().getKeyStrokes() != null &&
                    !e.getKeyPart().getKeyStrokes().isEmpty() &&
                    e.getKeyPart().getKeyStrokes().getFirst() != null)
                modeService.switchCurrentMode(e.getKeyPart().getKeyStrokes().getFirst());
            else
                modeSelector.getApplication().render(null, "");

        if (e.getKeyPart().getKeyEvt().equals("SCENE_RESET"))
            modeService.switchCurrentMode("DESKTOP");

        if (!modeService.getCurrentMode().handleEvent(e.getKeyPart().getKeyEvt(), e.getKeyPart().getKeyStrokes()))
            modeService.getDesktopModule().handleEvent(e.getKeyPart().getKeyEvt(), e.getKeyPart().getKeyStrokes());
    }

    @Component
    class ModeEventRouter implements ApplicationListener<ModeEvent> {

        @Override
        public void onApplicationEvent(ModeEvent event) {
            modeService.switchCurrentMode(event.getMode());
            modeSelector.getApplication().close();
        }
    }
}
