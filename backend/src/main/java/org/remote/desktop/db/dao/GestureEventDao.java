package org.remote.desktop.db.dao;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.entity.GestureEvent;
import org.remote.desktop.db.repository.EventRepository;
import org.remote.desktop.db.repository.GestureEventRepository;
import org.remote.desktop.db.repository.GestureRepository;
import org.remote.desktop.model.vto.GestureEventVto;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.remote.desktop.util.FluxUtil.*;

@Service
@Transactional
@RequiredArgsConstructor
public class GestureEventDao {

    private final EventRepository eventRepository;
    private final GestureRepository gestureRepository;
    private final GestureEventRepository gestureEventRepository;

    public Long createGestureOnEvent(Long id) {
        return Optional.of(new GestureEvent())
                .flatMap(q -> eventRepository.findById(id)
                        .map(q::withEvent))
                .map(gestureEventRepository::save)
                .map(GestureEvent::getEvent)
                .map(eventRepository::save)
                .map(Event::getGestureEvent)
                .map(GestureEvent::getId)
                .orElseThrow();
    }

    public void updateEventGesture(GestureEventVto vto) {
        gestureEventRepository.findById(vto.getId())
                .map(q -> q.withLeftStickGesture(optToNull(vto.getLeftStickGestureFk(), gestureRepository::findById))                )
                .map(q -> q.withRightStickGesture(optToNull(vto.getRightStickGestureFk(), gestureRepository::findById)))
                .ifPresent(gestureEventRepository::save);
    }

    public void deleteGestureEvent(Long gestureEventId) {
        gestureEventRepository.deleteById(gestureEventId);
    }
}
