package org.remote.desktop.db.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.db.entity.Gesture;
import org.remote.desktop.db.entity.GesturePath;
import org.remote.desktop.db.repository.GesturePathRepository;
import org.remote.desktop.db.repository.GestureRepository;
import org.remote.desktop.mapper.GestureMapper;
import org.remote.desktop.model.vto.GesturePathVto;
import org.remote.desktop.model.vto.GestureVto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class GestureDao {

    public static final String GESTURES_CACHE_NAME = "gestures";

    private final GesturePathRepository gesturePathRepository;
    private final GestureRepository gestureRepository;

    private final GestureMapper gestureMapper;

    @Cacheable(GESTURES_CACHE_NAME)
    public List<GestureVto> getAllGestures() {
        return gestureRepository.findAll().stream()
                .map(gestureMapper::map)
                .toList();
    }

    public GestureVto getGestureById(long id) {
        return gestureRepository.findById(id)
                .map(gestureMapper::map)
                .orElseThrow();
    }

    public Function<String, GesturePathVto> addGesturePath(long gestureId) {
        return path -> gestureRepository.findById(gestureId)
                .map(q -> GesturePath.builder()
                        .gesture(q)
                        .path(path)
                        .build()
                )
                .map(gesturePathRepository::save)
                .map(gestureMapper::map)
                .orElseThrow();
    }

    public Long createNew() {
        return Optional.of(new Gesture())
                .map(gestureRepository::save)
                .map(Gesture::getId)
                .orElseThrow();
    }

    public void updatePathOn(Long id, String newPath) {
        gesturePathRepository.findById(id)
                .ifPresent(q -> q.setPath(newPath));
    }

    public Consumer<String> updateName(Long id) {
        return name -> gestureRepository.findById(id)
                .ifPresent(q -> q.setName(name));
    }

    public void deletePath(Long id) {
        gesturePathRepository.deleteById(id);
    }

    public void deleteGesture(Long id) {
        gestureRepository.deleteById(id);
    }
}
