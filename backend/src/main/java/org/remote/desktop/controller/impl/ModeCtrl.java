package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.vto.ModeVto;
import org.remote.desktop.service.impl.ModeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/event/mode")
@RequiredArgsConstructor
public class ModeCtrl {

    private final ModeService modeService;

    @GetMapping("all")
    public List<ModeVto> getAllModes() {
        return modeService.getAllModes();
    }
}
