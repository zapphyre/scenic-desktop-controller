package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.vto.EventVto;
import org.remote.desktop.service.impl.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/event")
@RequiredArgsConstructor
public class EventCtrl {

    private final EventService eventService;

    @GetMapping("inherents/{sceneId}")
    public List<EventVto> getInherentsForScene(@PathVariable("sceneId") long sceneId) {
        return eventService.getInherentsRecurcivelyFor(sceneId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveGamepadEvent(@RequestBody EventVto gamepadEventVto) {
        return eventService.create(gamepadEventVto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGamepadAction(@RequestBody EventVto gamepadEventVto) {
        eventService.update(gamepadEventVto);
    }

    @DeleteMapping("{eventId}")
    public void delete(@PathVariable("eventId") Long eventId) {
        eventService.delete(eventId);
    }
}
