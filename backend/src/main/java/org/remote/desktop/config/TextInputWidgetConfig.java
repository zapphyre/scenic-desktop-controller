package org.remote.desktop.config;

import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.ui.InputWidget;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TextInputWidgetConfig {

    private final SettingsDao settingsDao;

    @Bean
    public InputWidget inputWidget() {
        return new InputWidget(2, Color.BURLYWOOD, 0.4, Color.ORANGE, Color.BLACK, 6, settingsDao.getSettings().getTextInputSceneName());
    }
}
