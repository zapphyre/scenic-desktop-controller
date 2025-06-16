package org.remote.desktop.db.dao;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.XdoAction;
import org.remote.desktop.db.repository.EventRepository;
import org.remote.desktop.db.repository.XdoActionRepository;
import org.remote.desktop.mapper.XdoActionMapper;
import org.remote.desktop.model.vto.XdoActionVto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.remote.desktop.util.FluxUtil.optToNull;

@Service
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
                .ifPresent(xdoActionMapper.update(vto, optToNull(vto.getEventFk(), eventRepository::findById)));
    }

    public Mono<Long> create(XdoActionVto vto) {
        return Mono.just(vto)
                .map(xdoActionMapper.map(optToNull(vto.getEventFk(), eventRepository::findById)))
                .map(xdoActionRepository::save)
                .map(XdoAction::getId);
    }
}
