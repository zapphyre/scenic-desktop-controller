package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.ui.InputWidget;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static jxdotool.xDoToolUtil.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandActuator implements ApplicationListener<XdoCommandEvent> {

    private final SceneStateRepository actuatedStateRepository;
    private final InputWidget inputWidget;

    @Override
    @SneakyThrows
    public void onApplicationEvent(XdoCommandEvent e) {
        String xdoKeyPart = String.join("+", e.getKeyPart().getKeyStrokes());

        switch (e.getKeyPart().getKeyEvt()) {
            case PRESS -> keydown(xdoKeyPart);
            case STROKE -> pressKey(xdoKeyPart);
            case RELEASE -> keyup(xdoKeyPart);
            case CLICK -> click(xdoKeyPart);
            case MOUSE_DOWN -> xDo("mousedown", xdoKeyPart);
            case MOUSE_UP -> xDo("mouseup", xdoKeyPart);
            case TIMEOUT -> Thread.sleep(Integer.parseInt(xdoKeyPart));
            case SCENE_RESET -> actuatedStateRepository.nullifyForcedScene();
            case KEYBOARD_ON -> inputWidget.render();
            case KEYBOARD_OFF -> inputWidget.clearText();
            case BUTTON -> buttonMapped(e.getSourceSceneWindowName(), e.getTrigger());
        }
    }

    @SneakyThrows
    private void buttonMapped(String sourceSceneWindowName, String trigger) {
        System.out.println("buttonMapped: " + sourceSceneWindowName + " " + trigger);

        if (trigger.equalsIgnoreCase(EButtonAxisMapping.X.name()))
            inputWidget.addCharacter(" ");

        if (trigger.equalsIgnoreCase(EButtonAxisMapping.A.name()))
            xDo("type", inputWidget.getFullContentClearClose());

        if (trigger.equalsIgnoreCase(EButtonAxisMapping.Y.name()))
            inputWidget.deleteLast();

        if (trigger.equalsIgnoreCase(EButtonAxisMapping.BUMPER_RIGHT.name()))
            inputWidget.addSelectedLetter();

    }
}
