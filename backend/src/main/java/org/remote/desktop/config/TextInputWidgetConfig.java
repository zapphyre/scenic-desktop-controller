package org.remote.desktop.config;

import com.arun.trie.base.Trie;
import com.arun.trie.base.ValueFrequency;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.prediction.G4Trie;
import org.remote.desktop.ui.CircleButtonsInputWidget;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class TextInputWidgetConfig {

    private final SettingsDao settingsDao;
    private final Trie<String> trie;

    @Bean
    public CircleButtonsInputWidget inputWidget() {
        CircleButtonsInputWidget variableGroupingInputWidget = new CircleButtonsInputWidget(90,2, Color.BURLYWOOD,
                0.4, Color.ORANGE, Color.BLACK,
                6, settingsDao.getSettings().getTextInputSceneName());

        variableGroupingInputWidget.setPredictor(q -> trie.getValueFreqSuggestions(q).stream()
                        .sorted().map(ValueFrequency::getValue).toList()
        );

        return variableGroupingInputWidget;
    }
}
