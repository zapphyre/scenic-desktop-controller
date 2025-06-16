package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.GestureEventDao;
import org.remote.desktop.model.vto.GestureEventVto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GestureEventService {

    private final GestureEventDao gestureEventDao;

    public Long createGestureEvent(Long id) {
        return gestureEventDao.createGestureOnEvent(id);
    }

    public void updateEventGesture(GestureEventVto vto) {
        gestureEventDao.updateEventGesture(vto);
    }

    public void deleteGestureEvent(Long id) {
        gestureEventDao.deleteGestureEvent(id);
    }
}
