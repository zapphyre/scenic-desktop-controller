package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.remote.desktop.model.event.ModeEvent;
import org.remote.desktop.service.impl.ModeService;
import org.remote.desktop.ui.select.mode.ModeSelector;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GpadActuatorRouter implements ApplicationListener<GpadCommandEvent> {

    private final ModeSelector modeSelector;
    private final ModeService modeService;

    @Override
    public void onApplicationEvent(GpadCommandEvent e) {
        // do smting about this ifs

        if (e.getKeyPart().getKeyEvt().equals("MODE_SELECT"))
            if (e.getKeyPart().getKeyStrokes() != null &&
                    !e.getKeyPart().getKeyStrokes().isEmpty() &&
                    e.getKeyPart().getKeyStrokes().getFirst() != null)
                modeService.switchCurrentMode(e.getKeyPart().getKeyStrokes().getFirst(), e.getDevice());
            else
                modeSelector.getApplication().render(null, "", e.getDevice());

        if (e.getKeyPart().getKeyEvt().equals("SCENE_RESET"))
            modeService.switchCurrentMode("DESKTOP", e.getDevice());

        if (!modeService.getCurrentModeFor(e.getDevice()).handleEvent(e.getKeyPart().getKeyEvt(), e.getKeyPart().getKeyStrokes()))
            modeService.getDesktopModule().handleEvent(e.getKeyPart().getKeyEvt(), e.getKeyPart().getKeyStrokes());
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }

    @Component
    class ModeEventRouter implements ApplicationListener<ModeEvent> {

        @Override
        public void onApplicationEvent(ModeEvent event) {
            modeService.switchCurrentMode(event.getMode(), event.getDevice()).activate();
            modeSelector.getApplication().close();
        }
    }
}
