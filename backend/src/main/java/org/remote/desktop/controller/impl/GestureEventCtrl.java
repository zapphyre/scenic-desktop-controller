package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.vto.GestureEventVto;
import org.remote.desktop.service.impl.GestureEventService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/event/{eventId}/gesture")
@RequiredArgsConstructor
public class GestureEventCtrl {

    private final GestureEventService gestureEventService;

    @PostMapping
    public Long createGestureEvent(@PathVariable("eventId") Long id) {
        return gestureEventService.createGestureEvent(id);
    }

    @PutMapping
    public void updateGesture(@RequestBody GestureEventVto vto) {
        gestureEventService.updateEventGesture(vto);
    }

    @DeleteMapping("{gestureId}")
    public void deleteGestureEvent(@PathVariable("gestureId") Long id) {
        gestureEventService.deleteGestureEvent(id);
    }
}
