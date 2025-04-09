package org.remote.desktop.config;

import javafx.scene.paint.Color;
import org.remote.desktop.ui.InputWidget;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TextInputWidgetConfig {

    @Bean
    public InputWidget inputWidget() {
        return new InputWidget(2, Color.BURLYWOOD, 0.4, Color.ORANGE, Color.BLACK, 6);
    }
}
