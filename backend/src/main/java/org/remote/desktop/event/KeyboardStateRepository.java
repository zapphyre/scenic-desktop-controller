package org.remote.desktop.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.remote.desktop.pojo.KeyPart;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class KeyboardStateRepository implements ApplicationListener<XdoCommandEvent> {

    @Getter
    private final Set<KeyPart> pressedKeys = new HashSet<>();
    private final List<Consumer<KeyPart>> issuedCommandObservers = new LinkedList<>();

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onApplicationEvent(XdoCommandEvent event) {
        if (event.getKeyPart().getKeyEvt().ordinal() > 1) return;

        switch (event.getKeyPart().getKeyEvt()) {
            case PRESS -> pressedKeys.add(event.getKeyPart());
            case RELEASE -> pressedKeys.remove(event.getKeyPart().invert());
        }

        issuedCommandObservers.forEach(q -> q.accept(event.getKeyPart()));
    }

    public void registerXdoCommandObserver(Consumer<KeyPart> observer) {
        issuedCommandObservers.add(observer);
    }

    public void issueKeyupCommand(KeyPart keyPart) {
        eventPublisher.publishEvent(new XdoCommandEvent(this, keyPart.getKeyEvt(), keyPart.getKeyStrokes(), null));
    }

    public void releaseAllPressedKeys() {
        pressedKeys.stream()
                .map(q -> new XdoCommandEvent(q, this))
                .forEach(eventPublisher::publishEvent);
    }
}