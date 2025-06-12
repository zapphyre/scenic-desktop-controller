package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.vto.GestureVto;
import org.remote.desktop.service.GestureService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/gestures")
@RequiredArgsConstructor
public class GestureCtrl {


    private final GestureService gestureService;

    @GetMapping
    public List<GestureVto> getGestures() {
        return gestureService.getAllGestures();
    }
}
