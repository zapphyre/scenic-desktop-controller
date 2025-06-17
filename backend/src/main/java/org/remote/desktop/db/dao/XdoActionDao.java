package org.remote.desktop.db.dao;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.XdoAction;
import org.remote.desktop.db.repository.EventRepository;
import org.remote.desktop.db.repository.XdoActionRepository;
import org.remote.desktop.mapper.XdoActionMapper;
import org.remote.desktop.model.vto.XdoActionVto;
import org.remote.desktop.util.FluxUtil;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class XdoActionDao {

    private final XdoActionRepository xdoActionRepository;
    private final EventRepository eventRepository;
    private final XdoActionMapper xdoActionMapper;

    public void delete(Long xdoActionId) {
        xdoActionRepository.deleteById(xdoActionId);
    }

    public List<String> getAllCurrentXdoStrokes() {
        return xdoActionRepository.findAll().stream()
                .map(XdoAction::getKeyStrokes)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }

    public void update(XdoActionVto vto) {
        Optional.of(vto)
                .map(XdoActionVto::getId)
                .flatMap(xdoActionRepository::findById)
                .ifPresent(xdoActionMapper.update(vto, FluxUtil.optToNull(vto.getEventFk(), eventRepository::findById)));
    }

    public Long create(XdoActionVto vto) {
        return Optional.of(vto)
                .map(xdoActionMapper.map(FluxUtil.optToNull(vto.getEventFk(), eventRepository::findById)))
                .map(xdoActionRepository::save)
                .map(XdoAction::getId)
                .orElseThrow();
    }
}
