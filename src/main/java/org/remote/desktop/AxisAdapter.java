package org.remote.desktop;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.service.JoyWorker;
import org.remote.desktop.actuate.MouseCtrl;
import org.springframework.stereotype.Component;

import static org.asmus.builder.AxisEventFactory.leftStickStream;
import static org.asmus.builder.AxisEventFactory.rightStickStream;

@Component
@RequiredArgsConstructor
public class AxisAdapter {

    private final JoyWorker worker;

    @PostConstruct
    void initLeftAxis() {
        leftStickStream().polarProducer(worker)
                .subscribe(MouseCtrl::moveMouse);

        rightStickStream().polarProducer(worker)
                .subscribe(MouseCtrl::scroll);
    }
}
