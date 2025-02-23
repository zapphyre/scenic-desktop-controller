package org.remote.desktop.event;

import org.remote.desktop.model.event.XdoCommandEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class KeyboardStateRepository implements ApplicationListener<XdoCommandEvent> {

    List<XdoCommandEvent> pressedKeys = new LinkedList<>();

    List<Consumer<String>> keyPressObservers = new LinkedList<>();
    List<Consumer<String>> keyReleaseObservers = new LinkedList<>();

    @Override
    public void onApplicationEvent(XdoCommandEvent event) {
        switch (event.getKeyEvt()) {
            case PRESS -> keyPressObservers.forEach(q -> q.accept(event.getKeyPress()));
            case RELEASE -> keyReleaseObservers.forEach(q -> q.accept(event.getKeyPress()));
        }
    }
}
