package org.remote.desktop.component;

import lombok.RequiredArgsConstructor;
import org.asmus.model.PolarCoords;
import org.asmus.service.JoyWorker;
import org.remote.desktop.actuate.MouseCtrl;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class AxisAdapter {

    private final JoyWorker worker;

    public Consumer<PolarCoords> getMouseConsumer() {
        return MouseCtrl::moveMouse;
    }

    public Consumer<PolarCoords> getScrollConsumer() {
        return MouseCtrl::scroll;
    }
}
