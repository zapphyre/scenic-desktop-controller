package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

import static jxdotool.xDoToolUtil.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandActuator implements ApplicationListener<XdoCommandEvent> {

    private final SceneStateRepository actuatedStateRepository;

    @Override
    @SneakyThrows
    public void onApplicationEvent(XdoCommandEvent e) {
        if (e.getKeyPart().getKeyEvt() == EKeyEvt.STROKE &&
                Objects.isNull(e.getKeyPart().getKeyStrokes())
        ) {
            log.error("!!!! event: {} is null !!!!!", e.getKeyPart().getKeyEvt());
            return;
        }

        String xdoKeyPart = String.join("+", e.getKeyPart().getKeyStrokes());
        System.out.println("applying: " + e.getKeyPart().getKeyEvt() + " stroke: " + xdoKeyPart);

        switch (e.getKeyPart().getKeyEvt()) {
            case PRESS -> keydown(xdoKeyPart);
            case STROKE -> pressKey(xdoKeyPart);
            case RELEASE -> keyup(xdoKeyPart);
            case CLICK -> click(xdoKeyPart);
            case MOUSE_DOWN -> xDo("mousedown", xdoKeyPart);
            case MOUSE_UP -> xDo("mouseup", xdoKeyPart);
            case TIMEOUT -> Thread.sleep(Integer.parseInt(xdoKeyPart));
            case SCENE_RESET -> actuatedStateRepository.nullifyForcedScene();
        }
    }
}
