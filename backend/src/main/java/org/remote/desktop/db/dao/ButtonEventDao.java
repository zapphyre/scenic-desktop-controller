package org.remote.desktop.db.dao;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.ButtonEvent;
import org.remote.desktop.db.entity.Event;
import org.remote.desktop.db.repository.ButtonEventRepository;
import org.remote.desktop.db.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ButtonEventDao {

    private final ButtonEventRepository buttonEventRepository;
    private final EventRepository eventRepository;

    public void delete(Long id) {
        buttonEventRepository.deleteById(id);
    }

    public Long createButtonEventOnEvent(Long eventId) {
        return Optional.of(new ButtonEvent())
                .flatMap(q -> eventRepository.findById(eventId)
                        .map(p -> p.withButtonEvent(q)))
                .map(eventRepository::save)
                .map(Event::getButtonEvent)
                .map(buttonEventRepository::save)
                .map(ButtonEvent::getId)
                .orElseThrow();
    }
}
