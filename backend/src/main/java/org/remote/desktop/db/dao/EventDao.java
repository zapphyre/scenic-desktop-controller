package org.remote.desktop.db.dao;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.db.repository.EventRepository;
import org.remote.desktop.db.repository.SceneRepository;
import org.remote.desktop.mapper.EventMapper;
import org.remote.desktop.model.vto.EventVto;
import org.remote.desktop.util.FluxUtil;
import org.remote.desktop.util.RecursiveScraper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventDao {

    private final EventRepository eventRepository;
    private final SceneRepository sceneRepository;

    private final EventMapper eventMapper;

    private final RecursiveScraper<Event, Scene> scraper = new RecursiveScraper<>();

    public void delete(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public Long create(EventVto vto) {
        return Optional.of(vto)
                .map(eventMapper.map(FluxUtil.optToNull(vto.getParentFk(), sceneRepository::findById),
                        FluxUtil.optToNull(vto.getNextSceneFk(), sceneRepository::findById))
                )
                .map(eventRepository::save)
                .map(Event::getId)
                .orElseThrow();
    }

    public void update(EventVto vto) {
        eventRepository.findById(vto.getId())
                .ifPresent(eventMapper.update(vto,
                        FluxUtil.optToNull(vto.getParentFk(), sceneRepository::findById),
                        FluxUtil.optToNull(vto.getNextSceneFk(), sceneRepository::findById)
                ));
    }

    public List<EventVto> getInherentsRecurcivelyFor(long sceneId) {
        return sceneRepository.findById(sceneId).stream()
                .map(scraper::scrapeActionsRecursive)
                .map(eventMapper::map)
                .flatMap(Collection::stream)
                .toList();
    }
}
