package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.model.event.ButtonEvent;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.ui.InputWidgetBase;
import org.remote.desktop.ui.model.EActionButton;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static jxdotool.xDoToolUtil.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandActuator implements ApplicationListener<XdoCommandEvent> {

    private final SceneStateRepository actuatedStateRepository;
    private final InputWidgetBase inputWidgetBase;
    protected final ApplicationEventPublisher eventPublisher;

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
            case KEYBOARD_ON -> inputWidgetBase.render();
            case KEYBOARD_OFF -> inputWidgetBase.close();
            case BUTTON -> eventPublisher.publishEvent(new ButtonEvent(this, EActionButton.valueOf(e.getTrigger())));
        }
    }

//    int idx = 0;
//    @SneakyThrows
//    private void buttonMapped(String sourceSceneWindowName, String trigger) {
//        System.out.println("buttonMapped: " + sourceSceneWindowName + " " + trigger);
//
////        inputWidget.setSecondaryText(List.of("foku", "meke", "ukulele", "ukulele"));
//
//        if (trigger.equalsIgnoreCase(EButtonAxisMapping.B.name()))
//            inputWidgetBase.setFrameOn(idx++);
//
//
//        if (trigger.equalsIgnoreCase(EButtonAxisMapping.X.name()))
//            inputWidgetBase.setFrameOn(idx--);
////            inputWidget.addCharacter(" ");
//
//        if (trigger.equalsIgnoreCase(EButtonAxisMapping.A.name()))
//            xDo("type", inputWidgetBase.getFullContentClearClose());
//
//        if (trigger.equalsIgnoreCase(EButtonAxisMapping.Y.name()))
//            inputWidgetBase.addSecondaryText(inputWidgetBase.getFullLettersContent());
////            inputWidget.deleteLast();
//
//        if (trigger.equalsIgnoreCase(EButtonAxisMapping.BUMPER_RIGHT.name()))
//            inputWidgetBase.addSelectedLetter();
//
//    }
}
