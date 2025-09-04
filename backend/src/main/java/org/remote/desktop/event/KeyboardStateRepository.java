package org.remote.desktop.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.EAdapterMode;
import org.remote.desktop.model.event.GpadCommandEvent;
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
public class KeyboardStateRepository implements ApplicationListener<GpadCommandEvent> {

    @Getter
    private final Set<KeyPart> pressedKeys = new HashSet<>();
    private final List<Consumer<KeyPart>> issuedCommandObservers = new LinkedList<>();

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onApplicationEvent(GpadCommandEvent event) {
//        if (event.getKeyPart().getKeyEvt().ordinal() > 1) return;
//
//        switch (event.getKeyPart().getKeyEvt()) {
//            case PRESS -> pressedKeys.add(event.getKeyPart());
//            case RELEASE -> pressedKeys.remove(event.getKeyPart().invert());
//        }

        issuedCommandObservers.forEach(q -> q.accept(event.getKeyPart()));
    }

    public void registerXdoCommandObserver(Consumer<KeyPart> observer) {
        issuedCommandObservers.add(observer);
    }

    public void issueKeyupCommand(KeyPart keyPart) {
        eventPublisher.publishEvent(new GpadCommandEvent(this, keyPart.getKeyEvt(), keyPart.getKeyStrokes(), EAdapterMode.DESKTOP, null, null, null, null, Set.of(), false));
    }

    public void releaseAllPressedKeys() {
        System.out.println("releasing pressed keys: " + pressedKeys);
        pressedKeys.stream()
                .map(q -> new GpadCommandEvent(q, this))
                .forEach(eventPublisher::publishEvent);
    }
}