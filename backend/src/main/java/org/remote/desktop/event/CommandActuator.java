package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static jxdotool.xDoToolUtil.*;

@Component
@RequiredArgsConstructor
public class CommandActuator implements ApplicationListener<XdoCommandEvent> {

    private final SceneStateRepository actuatedStateRepository;

    @Override
    @SneakyThrows
    public void onApplicationEvent(XdoCommandEvent e) {
        System.out.println("applying: " + e.getKeyPart().getKeyEvt() + " stroke: " + e.getKeyPart().getKeyPress());

        if (Objects.isNull(e.getKeyPart()) || e.getKeyPart().getKeyEvt() == EKeyEvt.STROKE &&
                Objects.isNull(e.getKeyPart().getKeyPress())
        ) return;

        switch (e.getKeyPart().getKeyEvt()) {
            case PRESS -> keydown(e.getKeyPart().getKeyPress());
            case STROKE -> pressKey(e.getKeyPart().getKeyPress());
            case RELEASE -> keyup(e.getKeyPart().getKeyPress());
            case CLICK -> click(e.getKeyPart().getKeyPress());
            case MOUSE_DOWN -> xDo("mousedown", e.getKeyPart().getKeyPress());
            case MOUSE_UP -> xDo("mouseup", e.getKeyPart().getKeyPress());
            case TIMEOUT -> Thread.sleep(Integer.parseInt(e.getKeyPart().getKeyPress()));
            case SCENE_RESET -> actuatedStateRepository.nullifyForcedScene();
        }
    }
}
