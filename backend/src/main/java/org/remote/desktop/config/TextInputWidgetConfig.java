package org.remote.desktop.config;

import com.arun.trie.base.Trie;
import com.arun.trie.base.ValueFrequency;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.dto.SettingDto;
import org.remote.desktop.service.impl.LanguageService;
import org.remote.desktop.service.impl.SceneService;
import org.remote.desktop.service.impl.XdoSceneService;
import org.remote.desktop.ui.CircleButtonsInputWidget;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class TextInputWidgetConfig {

    private final SettingsDao settingsDao;
    private final SceneService sceneService;
    private final Trie<String> trie;
    private final LanguageService languageService;
    private final XdoSceneService xdoSceneService;


    @Bean
    public CircleButtonsInputWidget inputWidget() {
        CircleButtonsInputWidget variableGroupingInputWidget = new CircleButtonsInputWidget(90, 2,
                Color.BURLYWOOD, 0.4, Color.ORANGE, Color.BLACK,
                6, settingsDao.getSettings().getTextInputSceneName(), q -> languageService.insertOrPropUp(1l),
                settingsDao.getSettings().isPersistentPreciseInput(), settingsDao::setPersistentInputMode, languageService.getAllDto()
        );

        variableGroupingInputWidget.setPredictor(q -> trie.getValueFreqSuggestions(q).stream()
                .sorted().map(ValueFrequency::getValue).toList()
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
