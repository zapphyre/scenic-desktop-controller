package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.PolarCoords;
import org.asmus.service.JoyWorker;
import org.remote.desktop.db.dao.GestureDao;
import org.remote.desktop.mapper.PolarCoordsMapper;
import org.remote.desktop.model.dto.rest.EStick;
import org.remote.desktop.model.dto.rest.NewGestureRequestDto;
import org.remote.desktop.model.vto.GesturePathVto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.zapphyre.fizzy.Gesturizer;
import org.zapphyre.fizzy.GesturizerAdapter;
import org.zapphyre.model.error.GestureTimeoutException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static org.asmus.builder.AxisEventFactory.leftStickStream;
import static org.asmus.builder.AxisEventFactory.rightStickStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PathService {

    private final JoyWorker worker;
    private final GestureDao gestureDao;
    private final PolarCoordsMapper polarCoordsMapper;
    private final Gesturizer gesturizer = Gesturizer.withDefaults();

    public void updatePathOn(Long id, String newPath) {
        gestureDao.updatePathOn(id, newPath);
    }

    public void deletePath(Long id) {
        gestureDao.deletePath(id);
    }

    public Mono<ResponseEntity<GesturePathVto>> catchGesture(NewGestureRequestDto req) {
        Flux<PolarCoords> coordsFlux = req.getStick() == EStick.RIGHT ?
                rightStickStream().polarProducer(worker) : leftStickStream().polarProducer(worker);

        return append(req.getId()).apply(coordsFlux);
    }

    Function<Flux<PolarCoords>, Mono<ResponseEntity<GesturePathVto>>> append(Long id) {
        GesturizerAdapter<PolarCoords> adapter = new GesturizerAdapter<>(gesturizer, polarCoordsMapper::map);

        return polarStream -> Mono.create(adapter.finishAfterFirst(polarStream))
                .map(gestureDao.addGesturePath(id))
                .map(ResponseEntity::ok)
                .doOnError(GestureTimeoutException.class, e -> log.error(e.getMessage()))
                .doOnTerminate(adapter::dispose)
                .onErrorResume(GestureTimeoutException.class, _ ->
                        Mono.just(ResponseEntity.status(HttpStatus.GONE).build())
                );
    }
}
