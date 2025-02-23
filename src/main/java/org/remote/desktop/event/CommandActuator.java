package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
        switch (e.getKeyEvt()) {
            case PRESS -> keydown(e.getKeyPress());
            case STROKE -> pressKey(e.getKeyPress());
            case RELEASE -> keyup(e.getKeyPress());
            case TIMEOUT -> Thread.sleep(Integer.parseInt(e.getKeyPress()));
            case SCENE_RESET -> actuatedStateRepository.nullifyForcedScene();
        }
    }
}
