package org.remote.desktop.config;

import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.dto.SettingDto;
import org.remote.desktop.service.impl.LanguageService;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.TrieService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.remote.desktop.ui.CircleButtonsInputWidget;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

import static org.remote.desktop.util.FluxUtil.asConsumer;

@Configuration
@RequiredArgsConstructor
public class TextInputWidgetConfig {

    private final SettingsDao settingsDao;
    private final SceneService sceneService;
    private final TrieService trieService;
    private final LanguageService languageService;
    private final XdoSceneService xdoSceneService;

    @Bean
    public CircleButtonsInputWidget inputWidget() {
        CircleButtonsInputWidget variableGroupingInputWidget = new CircleButtonsInputWidget(90, 2,
                Color.BURLYWOOD, 0.4, Color.ORANGE, Color.BLACK,
                6, settingsDao.getSettings().getTextInputSceneName(),
                settingsDao.getSettings().isPersistentPreciseInput(), settingsDao::setPersistentInputMode,
                trieService::getTrie,
                languageService::getAllDto,
                q -> asConsumer(languageService.insertOrPropNonCommiting(q))
        );

        forceScene(variableGroupingInputWidget);

        return variableGroupingInputWidget;
    }

    void forceScene(CircleButtonsInputWidget widget) {
        Optional.of(settingsDao)
                .map(SettingsDao::getSettings)
                .map(SettingDto::getTextInputSceneName)
                .map(sceneService::getScene)
                .ifPresent(q -> widget.setKeyboardSceneActuator(() -> xdoSceneService.forceScene(q)));
    }
}
