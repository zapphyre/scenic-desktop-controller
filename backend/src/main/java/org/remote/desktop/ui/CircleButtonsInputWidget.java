package org.remote.desktop.ui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.remote.desktop.model.TrieGroupDef;
import org.remote.desktop.ui.component.FourButtonWidget;
import org.remote.desktop.ui.model.ButtonsSettings;
import org.remote.desktop.ui.model.EActionButton;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.remote.desktop.util.KeyboardLayoutTrieUtil.buttonDict;

public class CircleButtonsInputWidget extends VariableGroupingInputWidgetBase {

    public CircleButtonsInputWidget(double widgetSize, double letterSize, Color arcDefaultFillColor, double arcDefaultAlpha, Color highlightedColor, Color textColor, int letterGroupCount, String title) {
        super(widgetSize, letterSize, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, letterGroupCount, title);

        buttonDict.keySet().stream()
                .collect(Collectors.toMap(Function.identity(), q -> {
                    ButtonsSettings.ButtonsSettingsBuilder bs = ButtonsSettings.builder()
                            .textColor(Color.DARKGOLDENROD)
                            .baseColor(Color.BURLYWOOD)
                            .alpha(.8);
                    Map<EActionButton, TrieGroupDef> groupDefs = buttonDict.get(q);

                    Map<EActionButton, ButtonsSettings> settingsMap = Arrays.stream(EActionButton.values())
                            .map(groupDefs::get)
                            .collect(Collectors.toMap(TrieGroupDef::getButton,
                                    a -> bs.trieKey(a.getTrieCode())
                                            .letters(str(a.getElements()))
                                            .build()
                            ));

                    return new FourButtonWidget(settingsMap, (widgetSize * 2) * scaleFactor, 24);
                }, (p, q) -> q));
    }

    String str(List<String> lst) {
        return String.join(" ", lst);
    }

    @Override
    Pane getRightWidget() {
        ButtonsSettings bs = ButtonsSettings.builder()
                .baseColor(Color.BURLYWOOD)
                .alpha(.8)
                .textColor(Color.DARKGOLDENROD)
                .letters("A B C D")
                .build();

        return new FourButtonWidget(null, (widgetSize * 2) * scaleFactor, 24);
    }

    @Override
    public int setGroupActive(int index) {
        return 0;
    }

    @Override
    public char setElementActive(int index) {
        return 0;
    }
}
