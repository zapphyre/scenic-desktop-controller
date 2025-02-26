package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static jxdotool.xDoToolUtil.*;

@Component
@RequiredArgsConstructor
public class CommandActuator implements ApplicationListener<XdoCommandEvent> {

    private final SceneStateRepository actuatedStateRepository;

    @Override
    @SneakyThrows
    public void onApplicationEvent(XdoCommandEvent e) {
//        System.out.println("applying: " + e.getKeyPart().getKeyEvt() + " stroke: " + e.getKeyPart().getKeyPress());

        switch (e.getKeyPart().getKeyEvt()) {
            case PRESS -> keydown(e.getKeyPart().getKeyPress());
            case STROKE -> pressKey(e.getKeyPart().getKeyPress());
            case RELEASE -> keyup(e.getKeyPart().getKeyPress());
            case TIMEOUT -> Thread.sleep(Integer.parseInt(e.getKeyPart().getKeyPress()));
            case CLICK -> click(e.getKeyPart().getKeyPress());
            case SCENE_RESET -> actuatedStateRepository.nullifyForcedScene();
        }
    }
}
