package org.remote.desktop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asmus.mapper.PolarCoordsMapper;
import org.asmus.model.PolarCoords;
import org.asmus.service.JoyWorker;
import org.mapstruct.factory.Mappers;
import org.remote.desktop.db.dao.GestureDao;
import org.remote.desktop.model.dto.rest.EStick;
import org.remote.desktop.model.dto.rest.NewGestureRequestDto;
import org.remote.desktop.model.vto.GesturePathVto;
import org.remote.desktop.model.vto.GestureVto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.zapphyre.fizzy.Gesturizer;
import org.zapphyre.fizzy.GesturizerAdapter;
import org.zapphyre.model.error.GestureTimeoutException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static org.asmus.builder.AxisEventFactory.leftStickStream;
import static org.asmus.builder.AxisEventFactory.rightStickStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GestureService {

    private final Gesturizer gesturizer = Gesturizer.withDefaults();
    private final GestureDao gestureDao;
    private final JoyWorker worker;

    private final PolarCoordsMapper polarCoordsMapper = Mappers.getMapper(PolarCoordsMapper.class);

    public List<GestureVto> getAllGestures() {
        return gestureDao.getAllGestures();
    }

    public Long createNew() {
        return gestureDao.createNew();
    }

    public void updatePathOn(Long id, String newPath) {
        gestureDao.updatePathOn(id, newPath);
    }

    public void updateName(Long id, String name) {
        gestureDao.updateName(id, name);
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

    public void deleteGesture(Long id) {
        gestureDao.deleteGesture(id);
    }
}
