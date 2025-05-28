package org.remote.desktop.config;

import com.arun.trie.base.Trie;
import com.arun.trie.base.ValueFrequency;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.dto.SettingDto;
import org.remote.desktop.prediction.G4Trie;
import org.remote.desktop.service.XdoSceneService;
import org.remote.desktop.ui.CircleButtonsInputWidget;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class TextInputWidgetConfig {

    private final SettingsDao settingsDao;
    private final SceneDao sceneDao;
    private final Trie<String> trie;
    private final ApplicationEventPublisher eventPublisher;
    private final XdoSceneService xdoSceneService;


    @Bean
    public CircleButtonsInputWidget inputWidget() {
        CircleButtonsInputWidget variableGroupingInputWidget = new CircleButtonsInputWidget(90,2, Color.BURLYWOOD,
                0.4, Color.ORANGE, Color.BLACK,
                6, settingsDao.getSettings().getTextInputSceneName(), trie::incrementFrequency);

        variableGroupingInputWidget.setPredictor(q -> trie.getValueFreqSuggestions(q).stream()
                        .sorted().map(ValueFrequency::getValue).toList()
        );

        forcerScene(variableGroupingInputWidget);

        return variableGroupingInputWidget;
    }

    void forcerScene(CircleButtonsInputWidget widget) {
        Optional.of(settingsDao)
                .map(SettingsDao::getSettings)
                .map(SettingDto::getTextInputSceneName)
                .map(sceneDao::getScene)
                .ifPresent(q -> widget.setKeyboardSceneActuator(() -> xdoSceneService.forceScene(q)));
    }
}
