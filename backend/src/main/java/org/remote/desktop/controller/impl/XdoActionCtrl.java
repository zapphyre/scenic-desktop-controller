package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.vto.XdoActionVto;
import org.remote.desktop.service.impl.XdoActionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/action")
@RequiredArgsConstructor
public class XdoActionCtrl {

    private final XdoActionService xdoActionService;

    @GetMapping("all")
    public List<String> getAllCurrentXdoStrokes() {
        return xdoActionService.getAllCurrentXdoStrokes();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGamepadAction(@RequestBody XdoActionVto xdoActionVto) {
        xdoActionService.update(xdoActionVto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveXdoAction(@RequestBody XdoActionVto xdoActionVto) {
        return xdoActionService.create(xdoActionVto);
    }

    @DeleteMapping
    public void removeXdoAction(@RequestBody Long xdoActionId) {
        xdoActionService.delete(xdoActionId);
    }
}
