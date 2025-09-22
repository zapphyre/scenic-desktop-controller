package org.remote.desktop.config.selector;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.event.ModeEvent;
import org.remote.desktop.ui.select.SelectSelector;
import org.remote.desktop.ui.select.axis.AxisUiSelector;
import org.remote.desktop.ui.select.mode.ModeSelector;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class SelectorSelectorConfig {

    private final AxisUiSelector  axisUiSelector;
    private final ApplicationEventPublisher eventPublisher;

    @Bean
    public SelectSelector createSelectSelectApplication() {
        SelectSelector selectSelector = new SelectSelector();

        List<String> adjustable = List.of("RIGHT_TRIGGER", "LEFT_TRIGGER", "LEFT_STICK", "RIGHT_STICK");
        selectSelector.setItems(adjustable, Function.identity())
                .update(q -> eventPublisher.publishEvent(
                        new ModeEvent(this, q.getAnalogControl())
                ));

        return selectSelector;
    }
}
