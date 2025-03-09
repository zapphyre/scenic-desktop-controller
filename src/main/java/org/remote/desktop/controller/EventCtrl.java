package org.remote.desktop.controller;

import lombok.RequiredArgsConstructor;
import org.asmus.model.PolarCoords;
import org.asmus.model.TimedValue;
import org.asmus.service.JoyWorker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static org.asmus.builder.AxisEventFactory.leftStickStream;
import static org.asmus.builder.AxisEventFactory.rightStickStream;

@RestController
@RequestMapping("event")
@RequiredArgsConstructor
public class EventCtrl {

    private final JoyWorker worker;

    @GetMapping("button")
    public Flux<List<TimedValue>> getGpadButtonStateStream() {
        return worker.getButtonStream();
    }

    @GetMapping("axis")
    public Flux<Map<String, Integer>> getGpadAxisStateStream() {
        return worker.getAxisStream();
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
