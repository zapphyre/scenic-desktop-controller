package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.vto.GestureEventVto;
import org.remote.desktop.service.EventService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/event")
@RequiredArgsConstructor
public class EventCtrl {

    private final EventService eventService;

    @PutMapping("gesture")
    public void updateGesture(@RequestBody GestureEventVto vto) {
        eventService.updateEventGesture(vto);
    }

    @PostMapping("{id}/gesture")
    public Long createGestureEvent(@PathVariable("id") Long id) {
        return eventService.createGestureEvent(id);
    }

    @DeleteMapping("gesture/{id}")
    public void deleteGestureEvent(@PathVariable("id") Long id) {
        eventService.deleteGestureEvent(id);
    }
}
