package org.remote.desktop.mode.modul.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.desktop.remote.mode.GpadOsActionModule;
import org.remote.desktop.service.impl.StateService;

import java.util.List;

import static jxdotool.xDoToolUtil.*;

//@Component
@RequiredArgsConstructor
public class XdoActionModule implements GpadOsActionModule {

    private final StateService  stateService;

    @Override
    public String getName() {
        return "DESKTOP";
    }

    @Override
    public List<String> getNouns() {
        return List.of();
    }

    @Override
    public List<String> getVerbs() {
        return List.of();
    }

    @Override
    public boolean isScenic() {
        return true;
    }

    @SneakyThrows
    @Override
    public boolean handleEvent(String verb, List<String> noun) {
        String xdoKeyPart = String.join("+", noun);
//        System.out.println("xdoKeyPart: " + e);

        switch (verb) {
            case "PRESS" -> keydown(xdoKeyPart);
            case "STROKE" -> pressKey(xdoKeyPart);
            case "RELEASE" -> keyup(xdoKeyPart);
            case "CLICK" -> click(xdoKeyPart);
            case "MOUSE_DOWN" -> xDo("mousedown", xdoKeyPart);
            case "MOUSE_UP" -> xDo("mouseup", xdoKeyPart);
            case "TIMEOUT" -> Thread.sleep(Integer.parseInt(xdoKeyPart));
            case "SCENE_RESET" -> stateService.nullifyForced();
        }

        return true;
    }
}
