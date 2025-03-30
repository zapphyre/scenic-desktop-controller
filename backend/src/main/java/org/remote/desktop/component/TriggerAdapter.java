package org.remote.desktop.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.IntrospectedEventFactory;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class TriggerAdapter {

    private final ButtonPressMapper buttonPressMapper;
    private final IntrospectedEventFactory gamepadObserver;
    private final TriggerActionMatcher triggerActionMatcher;

    @PostConstruct
    void init() {
        gamepadObserver.getButtonEventStream()
                .map(buttonPressMapper::map)
                .flatMap(triggerActionMatcher.actionPickPipeline)
                .subscribe(System.out::println);
    }

    public Consumer<Map<String, Integer>> getLeftTriggerProcessor() {
        return gamepadObserver.leftTriggerStream()::processArrowEvents;
    }

    public Consumer<Map<String, Integer>> getRightTriggerProcessor() {
        return gamepadObserver.rightTriggerStream()::processArrowEvents;
    }
}
