package org.remote.desktop.config;

import com.arun.trie.base.Trie;
import com.arun.trie.io.TrieIO;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.ui.CircleButtonsInputWidget;
import org.remote.desktop.ui.InputWidgetBase;
import org.remote.desktop.ui.VariableGroupingInputWidgetBase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TextInputWidgetConfig {

    private final SettingsDao settingsDao;

    @Bean
    public CircleButtonsInputWidget inputWidget() {
        Trie<String> trie = TrieIO.loadTrie("slovak.trie");

        CircleButtonsInputWidget variableGroupingInputWidget = new CircleButtonsInputWidget(90,2, Color.BURLYWOOD,
                0.4, Color.ORANGE, Color.BLACK,
                6, settingsDao.getSettings().getTextInputSceneName());

//        inputWidget.setPredictor(trie::getValueSuggestions);

        return variableGroupingInputWidget;
    }
}
