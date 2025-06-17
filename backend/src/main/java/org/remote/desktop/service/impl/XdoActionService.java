package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.XdoActionDao;
import org.remote.desktop.model.vto.XdoActionVto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.remote.desktop.db.dao.SceneDao.SCENE_ACTIONS_CACHE_NAME;

@Service
@RequiredArgsConstructor
public class XdoActionService {

    private final XdoActionDao xdoActionDao;

    @Cacheable(SCENE_ACTIONS_CACHE_NAME)
    public List<String> getAllCurrentXdoStrokes() {
        return xdoActionDao.getAllCurrentXdoStrokes();
    }

    public Long create(XdoActionVto xdoActionVto) {
        return xdoActionDao.create(xdoActionVto);
    }

    public void update(XdoActionVto xdoActionVto) {
        xdoActionDao.update(xdoActionVto);
    }

    public void delete(Long xdoActionId) {
        xdoActionDao.delete(xdoActionId);
    }
}
