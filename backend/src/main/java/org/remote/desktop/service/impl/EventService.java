package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.EventDao;
import org.remote.desktop.model.vto.EventVto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.remote.desktop.db.dao.SceneDao.SCENE_INHERENTS_FOR_SCENE;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventDao eventDao;

    @Cacheable(SCENE_INHERENTS_FOR_SCENE)
    public List<EventVto> getInherentsRecurcivelyFor(long sceneId) {
        return eventDao.getInherentsRecurcivelyFor(sceneId);
    }

    public void delete(Long eventId) {
        eventDao.delete(eventId);
    }

    public Long create(EventVto eventVto) {
        return eventDao.create(eventVto);
    }

    public void update(EventVto eventVto) {
        eventDao.update(eventVto);
    }
}
