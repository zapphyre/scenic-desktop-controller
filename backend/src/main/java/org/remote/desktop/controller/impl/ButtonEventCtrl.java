package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.service.impl.ButtonEventService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/event/{eventId}/button")
@RequiredArgsConstructor
public class ButtonEventCtrl {

    private final ButtonEventService buttonEventService;

    @PostMapping
    public Long createButtonEvent(@PathVariable("eventId") Long eventId) {
        return buttonEventService.createButtonEventOnEvent(eventId);
    }

    @DeleteMapping("{buttonId}")
    public void delete(@PathVariable("eventId") Long eventId, @PathVariable("buttonId") Long buttonId) {
        buttonEventService.delete(buttonId);
    }
}
