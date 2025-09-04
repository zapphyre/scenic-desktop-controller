package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.XdoActionDao;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.vto.XdoActionVto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.winder.common.model.EWinderOp;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.remote.desktop.db.dao.SceneDao.SCENE_ACTIONS_CACHE_NAME;

@Service
@RequiredArgsConstructor
public class XdoActionService {

    private final XdoActionDao xdoActionDao;
    private final List<String> winderOps = Stream.of(EWinderOp.values()).map(EWinderOp::name).toList();

    @Cacheable(SCENE_ACTIONS_CACHE_NAME)
    public List<String> getAllCurrentXdoStrokes(EKeyEvt eKeyEvt) {
        return switch (eKeyEvt) {
            default -> xdoActionDao.getAllCurrentXdoStrokes().stream()
                    .filter(Predicate.not(winderOps::contains))
                    .toList();
        } ;
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
