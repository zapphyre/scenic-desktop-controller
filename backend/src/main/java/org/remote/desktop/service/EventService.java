package org.remote.desktop.service;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.EventDao;
import org.remote.desktop.model.vto.GestureEventVto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventDao eventDao;

    public void updateEventGesture(GestureEventVto vto) {
        eventDao.updateEventGesture(vto);
    }

    public Long createGestureEvent(Long id) {
        return eventDao.createGestureEvent(id);
    }

    public void deleteGestureEvent(Long id) {
        eventDao.deleteGestureEvent(id);
    }
}
