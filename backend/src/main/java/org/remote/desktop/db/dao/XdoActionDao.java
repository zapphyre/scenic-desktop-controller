package org.remote.desktop.db.dao;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.XdoAction;
import org.remote.desktop.db.repository.EventRepository;
import org.remote.desktop.db.repository.ModeRepository;
import org.remote.desktop.db.repository.XdoActionRepository;
import org.remote.desktop.mapper.EventMapper;
import org.remote.desktop.mode.model.EMode;
import org.remote.desktop.model.vto.XdoActionVto;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.zapphyre.function.FunHelper.optToNull;

@Service
@Transactional
@RequiredArgsConstructor
public class XdoActionDao {

    private final XdoActionRepository xdoActionRepository;
    private final EventRepository eventRepository;
    private final ModeRepository modeRepository;
    private final EventMapper eventMapper;

    public void delete(Long xdoActionId) {
        xdoActionRepository.deleteById(xdoActionId);
    }

    public List<String> getAllCurrentXdoStrokes() {
        return Stream.concat(xdoActionRepository.findAll().stream()
                .map(XdoAction::getKeyStrokes)
                .flatMap(Collection::stream)
                .distinct(), Arrays.stream(EMode.values()).map(Enum::name)
        ).toList();
    }

    public void update(XdoActionVto vto) {
        Optional.of(vto)
                .map(XdoActionVto::getId)
                .flatMap(xdoActionRepository::findById)
                .map(q -> q.withMode(modeRepository.findByAdapterMode(vto.getMode())))
                .ifPresent(eventMapper.update(vto, optToNull(vto.getEventFk(), eventRepository::findById)));
    }

    public Long create(XdoActionVto vto) {
        return Optional.of(vto)
                .map(eventMapper.mapXdoEvent(optToNull(vto.getEventFk(), eventRepository::findById)))
                .map(q -> q.withMode(modeRepository.findByAdapterMode(vto.getMode())))
                .map(xdoActionRepository::save)
                .map(XdoAction::getId)
                .orElseThrow();
    }
}
