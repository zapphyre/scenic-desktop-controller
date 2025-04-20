package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.event.keyboard.PredictionWidgetActuator;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.ui.InputWidgetBase;
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
    private final PredictionWidgetActuator widgetActuator;

    @Override
    @SneakyThrows
    public void onApplicationEvent(XdoCommandEvent e) {
        String xdoKeyPart = String.join("+", e.getKeyPart().getKeyStrokes());
        System.out.println("xdoKeyPart: " + e);

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
            case KEYBOARD_OFF -> xDo("type", inputWidgetBase.getSentenceAndReset());
            case KEYBOARD_LONG -> widgetActuator.longClick(e.getTrigger());
//            case BUTTON -> eventPublisher.publishEvent(new ButtonEvent(this, EActionButton.valueOf(e.getTrigger())));
        }
    }

}
