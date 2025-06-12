package org.remote.desktop.service;

import lombok.RequiredArgsConstructor;
import org.asmus.mapper.PolarCoordsMapper;
import org.asmus.model.PolarCoords;
import org.asmus.service.JoyWorker;
import org.mapstruct.factory.Mappers;
import org.remote.desktop.db.dao.GestureDao;
import org.remote.desktop.model.dto.rest.EStick;
import org.remote.desktop.model.dto.rest.NewGestureRequestDto;
import org.remote.desktop.model.vto.GestureVto;
import org.springframework.stereotype.Service;
import org.zapphyre.fizzy.Gesturizer;
import org.zapphyre.fizzy.GesturizerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static org.asmus.builder.AxisEventFactory.leftStickStream;
import static org.asmus.builder.AxisEventFactory.rightStickStream;

@Service
@RequiredArgsConstructor
public class GestureService {

    private final Gesturizer gesturizer = Gesturizer.withDefaults();
    private final GestureDao gestureDao;
    private final JoyWorker worker;

    private final PolarCoordsMapper polarCoordsMapper = Mappers.getMapper(PolarCoordsMapper.class);

    public Mono<GestureVto> catchGesture(NewGestureRequestDto req) {
        Flux<PolarCoords> coordsFlux = req.getStick() == EStick.RIGHT ?
                rightStickStream().polarProducer(worker) : leftStickStream().polarProducer(worker);

        return append(req.getId()).apply(coordsFlux);
    }

    Function<Flux<PolarCoords>, Mono<GestureVto>> append(Long id) {
        GesturizerAdapter<PolarCoords> adapter = new GesturizerAdapter<>(gesturizer, polarCoordsMapper::map);

        return polarStream -> Mono.create(adapter.finishAfterFirst(polarStream))
                .map(gestureDao.addGesturePath(id))
                .doOnTerminate(adapter::dispose);
    }

    public List<GestureVto> getAllGestures() {
        return gestureDao.getAllGestures();
    }
}
