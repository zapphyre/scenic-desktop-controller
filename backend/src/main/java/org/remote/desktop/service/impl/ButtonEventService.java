package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.ButtonEventDao;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ButtonEventService {

    private final ButtonEventDao buttonEventDao;

    public void delete(Long id) {
        buttonEventDao.delete(id);
    }

    public Long createButtonEventOnEvent(Long eventId) {
        return buttonEventDao.createButtonEventOnEvent(eventId);
    }
}
