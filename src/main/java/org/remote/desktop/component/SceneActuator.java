package org.remote.desktop.component;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.vto.XdoActionVto;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SceneActuator {

    List<XdoActionVto> keydown = new LinkedList<>();

    public void act(List<XdoActionVto> actions) {
        actions.forEach(p -> {
             if (p.getKeyEvt() == EKeyEvt.PRESS)
                keydown.add(p);

            if (p.getKeyEvt() == EKeyEvt.RELEASE)
                keydown.remove(p);


        });
    }

}
