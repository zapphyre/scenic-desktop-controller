package org.remote.desktop.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class KeyboardStateRepository implements ApplicationListener<XdoCommandEvent> {

    @Getter
    private final List<XdoCommandEvent> pressedKeys = new LinkedList<>();
    private final List<Consumer<XdoCommandEvent>> issuedCommandObservers = new LinkedList<>();

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onApplicationEvent(XdoCommandEvent event) {
        if (event.getKeyPart().getKeyEvt().ordinal() > 1) return;

        switch (event.getKeyPart().getKeyEvt()) {
            case PRESS -> pressedKeys.add(event);
            case RELEASE -> pressedKeys.remove(event.invert());
        }

        issuedCommandObservers.forEach(q -> q.accept(event));
    }

    public void registerXdoCommandObserver(Consumer<XdoCommandEvent> observer) {
        issuedCommandObservers.add(observer);
    }

    public void issueKeyupCommand(XdoCommandEvent event) {
        eventPublisher.publishEvent(event.invert());
    }
}
