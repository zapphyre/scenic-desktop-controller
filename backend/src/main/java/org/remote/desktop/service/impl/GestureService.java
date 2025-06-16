package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.db.dao.GestureDao;
import org.remote.desktop.model.vto.GestureVto;
import org.springframework.stereotype.Service;
import org.zapphyre.fizzy.Gesturizer;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GestureService {

    private final Gesturizer gesturizer = Gesturizer.withDefaults();
    private final GestureDao gestureDao;


    public Long createNew() {
        return gestureDao.createNew();
    }

    public List<GestureVto> getAllGestures() {
        return gestureDao.getAllGestures();
    }

    public void updateName(Long id, String name) {
        gestureDao.updateName(id, name);
    }

    public void deleteGesture(Long id) {
        gestureDao.deleteGesture(id);
    }
}
