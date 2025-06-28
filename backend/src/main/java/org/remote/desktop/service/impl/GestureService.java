package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.db.dao.GestureDao;
import org.remote.desktop.model.vto.GestureVto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class GestureService {

    private final GestureDao gestureDao;

    public Long createNew() {
        return gestureDao.createNew();
    }

    public List<GestureVto> getAllGestures() {
        return gestureDao.getAllGestures();
    }

    public Consumer<String> updateName(Long id) {
        return gestureDao.updateName(id);
    }

    public void deleteGesture(Long id) {
        gestureDao.deleteGesture(id);
    }
}
