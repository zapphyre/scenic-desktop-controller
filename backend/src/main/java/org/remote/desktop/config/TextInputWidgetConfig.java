package org.remote.desktop.config;

import com.arun.trie.base.Trie;
import com.arun.trie.io.TrieIO;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.ui.VariableGroupingInputWidget;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class TextInputWidgetConfig {

    private final SettingsDao settingsDao;
    private final List<String> buttons = List.of("Y", "B", "A", "X");

    @Bean
    public VariableGroupingInputWidget inputWidget() {
        Trie<String> trie = TrieIO.loadTrie("slovak.trie");

        VariableGroupingInputWidget variableGroupingInputWidget = new VariableGroupingInputWidget(2, Color.BURLYWOOD,
                0.4, Color.ORANGE, Color.BLACK,
                6, settingsDao.getSettings().getTextInputSceneName());

//        inputWidget.setPredictor(trie::getValueSuggestions);

        return variableGroupingInputWidget;
    }
}
