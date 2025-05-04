package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.actuate.MouseCtrl;
import org.remote.desktop.component.WinderHostRepository;
import org.remote.desktop.event.keyboard.PredictionWidgetActuator;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.model.event.keyboard.PredictionControlEvent;
import org.remote.desktop.service.XdoSceneService;
import org.remote.desktop.ui.InputWidgetBase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static jxdotool.xDoToolUtil.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandActuator implements ApplicationListener<XdoCommandEvent> {

    private final XdoSceneService xdoSceneService;
    private final InputWidgetBase inputWidgetBase;
    protected final ApplicationEventPublisher eventPublisher;
    private final PredictionWidgetActuator widgetActuator;
    private final WinderHostRepository winderHostRepository;

    @Override
    @SneakyThrows
    public void onApplicationEvent(XdoCommandEvent e) {
        String xdoKeyPart = String.join("+", e.getKeyPart().getKeyStrokes());
//        System.out.println("xdoKeyPart: " + e);

        switch (e.getKeyPart().getKeyEvt()) {
            case PRESS -> keydown(xdoKeyPart);
            case STROKE -> pressKey(xdoKeyPart);
            case RELEASE -> keyup(xdoKeyPart);
            case CLICK -> click(xdoKeyPart);
            case MOUSE_DOWN -> xDo("mousedown", xdoKeyPart);
            case MOUSE_UP -> xDo("mouseup", xdoKeyPart);
            case TIMEOUT -> Thread.sleep(Integer.parseInt(xdoKeyPart));
            case SCENE_RESET -> xdoSceneService.nullifyForcedScene();
            case KEYBOARD_ON -> inputWidgetBase.render();
            case KEYBOARD_OFF -> MouseCtrl.paste();
            case KEYBOARD_LONG -> widgetActuator.longClick(e.getTrigger());
            case BUTTON -> eventPublisher.publishEvent(
                    new PredictionControlEvent(this, null, null, e.getTrigger(), e.getModifiers(), e.isLongPress())
            );
            case WINDER_FF -> winderHostRepository.ff();
            case WINDER_RW -> winderHostRepository.rw();
        }
    }

}
