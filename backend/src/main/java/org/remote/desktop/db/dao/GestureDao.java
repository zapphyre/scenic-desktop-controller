package org.remote.desktop.db.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.db.entity.Gesture;
import org.remote.desktop.db.repository.GestureRepository;
import org.remote.desktop.mapper.GestureMapper;
import org.remote.desktop.model.vto.GestureVto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class GestureDao {

    private final GestureRepository gestureRepository;
    private final GestureMapper gestureMapper;

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

    public Function<String, GestureVto> addGesturePath(long gestureId) {
        return path -> gestureRepository.findById(gestureId)
                .filter(q -> q.getPaths().add(path))
                .map(gestureMapper::map)
                .orElseThrow();
    }

    public Long createNewForSceneEvent() {
        return gestureRepository.save(new Gesture()).getId();
    }
}
