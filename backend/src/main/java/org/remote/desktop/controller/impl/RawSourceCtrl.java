package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.asmus.model.PolarCoords;
import org.asmus.model.TimedValue;
import org.asmus.service.JoyWorker;
import org.remote.desktop.source.impl.LocalSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static org.asmus.builder.AxisEventFactory.leftStickStream;
import static org.asmus.builder.AxisEventFactory.rightStickStream;

@RestController
@RequestMapping("api/raw-event")
@RequiredArgsConstructor
public class RawSourceCtrl {

    private final JoyWorker worker;
    private final LocalSource source;

    @GetMapping("button")
    public Flux<List<TimedValue>> getGpadButtonStateStream() {
        return worker.getButtonStream()
                .doOnSubscribe(q -> source.disconnect())
                .doOnTerminate(source::connect);
    }

    @GetMapping("axis")
    public Flux<Map<String, Integer>> getGpadAxisStateStream() {
        return worker.getAxisStream().publish().autoConnect();
    }

    @GetMapping("left-stick")
    public Flux<PolarCoords> getLeftAxisPolarCoordsStream() {
        return leftStickStream().polarProducer(worker);
    }

    @GetMapping("right-stick")
    public Flux<PolarCoords> getRightAxisPolarCoordsStream() {
        return rightStickStream().polarProducer(worker);
    }
}
