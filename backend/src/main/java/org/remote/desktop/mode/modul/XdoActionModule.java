package org.remote.desktop.mode.modul;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.desktop.remote.mode.GpadOsActionModule;
import org.remote.desktop.service.impl.StateService;

import java.util.List;

import static jxdotool.xDoToolUtil.*;
import static org.remote.desktop.actuate.MouseAct.paste;
import static org.remote.desktop.mode.modul.KeyboardModule.copyToClipboard;

//@Component
@RequiredArgsConstructor
public class XdoActionModule implements GpadOsActionModule {

    private final StateService stateService;

    @Override
    public String getName() {
        return "DESKTOP";
    }

    @Override
    public List<String> getNouns() {
        return List.of("COPY", "PASTE");
    }

    @Override
    public List<String> getVerbs() {
        return List.of("CLIPBOARD");
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
        try {
            switch (verb) {
                case "PRESS" -> keydown(xdoKeyPart);
                case "STROKE" -> pressKey(xdoKeyPart);
                case "RELEASE" -> keyup(xdoKeyPart);
                case "CLICK" -> click(xdoKeyPart);
                case "MOUSE_DOWN" -> xDo("mousedown", xdoKeyPart);
                case "MOUSE_UP" -> xDo("mouseup", xdoKeyPart);
                case "TIMEOUT" -> Thread.sleep(Integer.parseInt(xdoKeyPart));
                case "SCENE_RESET" -> stateService.nullifyForced();
                case "CLIPBOARD" -> {
                    if (xdoKeyPart.equals("PASTE")) {
                        paste();
                    } else {
                        copyToClipboard("");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("action exception: " + e.getMessage());
            return false;
        }


        return true;
    }
}
