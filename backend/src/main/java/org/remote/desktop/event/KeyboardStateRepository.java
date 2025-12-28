package org.remote.desktop.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.remote.desktop.pojo.KeyPart;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class KeyboardStateRepository implements ApplicationListener<GpadCommandEvent> {

    @Getter
    private final Set<KeyPart> pressedKeys = new HashSet<>();
    private final List<Consumer<KeyPart>> issuedCommandObservers = new LinkedList<>();

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onApplicationEvent(GpadCommandEvent event) {
//        System.out.printf("GpadCommandEvent: %s\n", event);

        Optional.of(event)
                .map(GpadCommandEvent::getKeyPart)
                .filter(keyPart -> switch (keyPart.getKeyEvt()) {
                    case "PRESS" -> pressedKeys.add(keyPart);
                    case "RELEASE" -> pressedKeys.remove(keyPart.invert());
                    default -> true;
                })
                .ifPresent(q -> issuedCommandObservers.forEach(p -> p.accept(q)));
    }

    public void registerXdoCommandObserver(Consumer<KeyPart> observer) {
        issuedCommandObservers.add(observer);
    }

    public void issueKeyupCommand(KeyPart keyPart) {
        eventPublisher.publishEvent(new GpadCommandEvent(this, keyPart.getKeyEvt(), keyPart.getKeyStrokes(), null, null, null, null, Set.of(), false, null));
    }

    public void releaseAllPressedKeys() {
        System.out.println("releasing pressed keys: " + pressedKeys);
        pressedKeys.stream()
                .map(q -> new GpadCommandEvent(q, this))
                .forEach(eventPublisher::publishEvent);
    }
}