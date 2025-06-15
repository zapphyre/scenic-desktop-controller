package org.remote.desktop.db.dao;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.entity.GestureEvent;
import org.remote.desktop.db.repository.EventRepository;
import org.remote.desktop.db.repository.GestureEventRepository;
import org.remote.desktop.db.repository.GestureRepository;
import org.remote.desktop.mapper.ButtonEventMapper;
import org.remote.desktop.mapper.EventMapper;
import org.remote.desktop.mapper.GestureEventMapper;
import org.remote.desktop.model.vto.GestureEventVto;
import org.remote.desktop.util.FluxUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.remote.desktop.db.dao.SceneDao.*;

@Service
@Transactional
@RequiredArgsConstructor
public class EventDao {

    private final EventRepository eventRepository;
    private final GestureEventRepository gestureEventRepository;
    private final GestureRepository geometryRepository;

    private final EventMapper eventMapper;
    private final GestureEventMapper gestureEventMapper;
    private final ButtonEventMapper buttonEventMapper;

    @CacheEvict(value = {SCENE_CACHE_NAME, SCENE_ACTIONS_CACHE_NAME, SCENE_LIST_CACHE_NAME}, allEntries = true)
    public void updateEventGesture(GestureEventVto vto) {
        gestureEventRepository.findById(vto.getId())
                .map(q -> q.withLeftStickGesture(FluxUtil.optToNull(vto.getLeftStickGestureFk(), geometryRepository::findById))                )
                .map(q -> q.withRightStickGesture(FluxUtil.optToNull(vto.getRightStickGestureFk(), geometryRepository::findById)))
                .ifPresent(gestureEventRepository::save);
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, SCENE_ACTIONS_CACHE_NAME, SCENE_LIST_CACHE_NAME}, allEntries = true)
    public void deleteGestureEvent(Long gestureEventId) {
        gestureEventRepository.deleteById(gestureEventId);
    }

    @CacheEvict(value = {SCENE_CACHE_NAME, SCENE_ACTIONS_CACHE_NAME, SCENE_LIST_CACHE_NAME}, allEntries = true)
    public Long createGestureEvent(Long id) {
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
}
