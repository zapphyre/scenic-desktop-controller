package org.remote.desktop.ui;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.remote.desktop.model.TrieGroupDef;
import org.remote.desktop.ui.component.FourButtonWidget;
import org.remote.desktop.ui.model.ButtonsSettings;
import org.remote.desktop.ui.model.EActionButton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.remote.desktop.util.KeyboardLayoutTrieUtil.buttonDict;

public class CircleButtonsInputWidget extends VariableGroupingInputWidgetBase {

    private final Map<Integer, FourButtonWidget> groupWidgetMap;

    public CircleButtonsInputWidget(double widgetSize, double letterSize, Color arcDefaultFillColor, double arcDefaultAlpha, Color highlightedColor, Color textColor, int letterGroupCount, String title) {
        super(widgetSize, letterSize, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, letterGroupCount, title);
        ButtonsSettings.ButtonsSettingsBuilder bs = ButtonsSettings.builder()
                .textColor(Color.DARKGOLDENROD)
                .baseColor(Color.BURLYWOOD)
                .alpha(.8);

        groupWidgetMap = buttonDict.keySet().stream()
                .collect(Collectors.toMap(Function.identity(), q -> {
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

        rightPane.getChildren().add(activeButtonGroup = groupWidgetMap.get(0));
    }

    String str(List<String> lst) {
        return String.join(" ", lst);
    }

    Pane rightPane = new Pane();
    @Override
    Pane createRightWidget() {
        return rightPane;
    }

    FourButtonWidget activeButtonGroup;
    @Override
    public int setGroupActive(int index) {
        Platform.runLater(() -> {
            rightPane.getChildren().clear();
            rightPane.getChildren().add(activeButtonGroup = groupWidgetMap.get(index));
        });

        getGroupWidget().selectSegment(index);

        return index + 1;
    }

    @Override
    public char setActive(int index) {
        return activeButtonGroup.activate(EActionButton.values()[index]);
    }

    StringBuilder key = new StringBuilder();
    @Override
    public void setActiveAndType(int index) {
        key.append(setActive(index));

        Platform.runLater(() -> wordsContainer.clear());
        Platform.runLater(() -> {
            predictions = new LinkedList<>(predictor.apply(key.toString()));

            List<String> limitedPredictions = filterWordsByCharLimit(predictions, fittingCharacters);
            long count = limitedPredictions.stream().mapToInt(String::length).sum();
            System.out.println("letter count: " + count);

            predictions.removeAll(limitedPredictions);

            System.out.println("Predictions: " + limitedPredictions);
            setWordsAvailable(limitedPredictions);
        });
    }

    @Override
    protected void resetPredictionStack() {
        key.setLength(0);
    }

    public static List<String> filterWordsByCharLimit(List<String> words, int maxChars) {
        List<String> result = new LinkedList<>();
        int currentCharCount = 0;

        for (String word : words) {
            // Calculate the character count for the current word
            // Add 1 for the space if the result list is not empty
            int wordLength = word.length();
            int additionalChars = wordLength; // + (result.isEmpty() ? 0 : 1);

            // Check if adding the word exceeds the max character limit
            if (currentCharCount + additionalChars <= maxChars) {
                result.add(word);
                currentCharCount += additionalChars;
            } else {
                // Stop if the next word would exceed the limit
                break;
            }
        }

        return result;
    }
}
