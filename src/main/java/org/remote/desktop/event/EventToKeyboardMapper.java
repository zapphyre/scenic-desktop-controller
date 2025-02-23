package org.remote.desktop.event;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.XdoCommandEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventToKeyboardMapper implements ApplicationListener<XdoCommandEvent> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onApplicationEvent(XdoCommandEvent event) {
        eventPublisher.publishEvent(event.mapToKeyboardEvent());
    }
}
