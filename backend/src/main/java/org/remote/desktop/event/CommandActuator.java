package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.model.event.XdoEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static jxdotool.xDoToolUtil.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandActuator implements ApplicationListener<XdoEvent> {

    @Override
    @SneakyThrows
    public void onApplicationEvent(XdoEvent e) {
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
//            case KEYBOARD_ON -> modeFactory.changeMode(EMode.KEYBOARD).currentModeEvent(e);
//            case KEYBOARD_OFF -> MouseAct.paste();
//            case KEYBOARD_LONG -> widgetActuator.longClick(e.getTrigger());
//            case BUTTON -> modeFactory.createEvent(e);
//            case BUTTON -> eventPublisher.publishEvent(
//                    new PredictionControlEvent(this, null, null, e.getTrigger(), e.getModifiers(), e.isLongPress())
//            );
//            case WINDER -> new WinderCommandEvent(this,
//                    EWinderOp.valueOf(e.getKeyPart().getKeyStrokes().getFirst())
//            );
//            case WINDER -> modeFactory.changeMode(EMode.WINDER);
//            case MODE -> {
//                log.info("SETTING MODE '{}'", xdoKeyPart);
//                modeService.setMode(EMode.valueOf(xdoKeyPart));
//            }
        }
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
