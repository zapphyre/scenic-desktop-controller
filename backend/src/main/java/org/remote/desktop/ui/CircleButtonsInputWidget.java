package org.remote.desktop.ui;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.remote.desktop.model.TrieGroupDef;
import org.remote.desktop.ui.component.FourButtonWidget;
import org.remote.desktop.ui.model.ButtonsSettings;
import org.remote.desktop.ui.model.EActionButton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.remote.desktop.util.KeyboardLayoutTrieUtil.buttonDict;

public class CircleButtonsInputWidget extends VariableGroupingInputWidgetBase {

    @Getter
    private final Map<Integer, FourButtonWidget> groupWidgetMap;

    @Getter
    private int groupActiveIndex;
    AtomicInteger letterIndex = new AtomicInteger(0);

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

        rightPane.getChildren().add(activeButtonGroup = groupWidgetMap.get(groupActiveIndex = 0));
        scheduleSizeResetOn = sizeReset.apply(activeButtonGroup.getTextSize());
    }

    private final Function<Double, Function<Consumer<Double>, Runnable>> resetTask =
            q -> p -> () -> {
                p.accept(q);
            };

    char currentChar;
    Future<?> pendingReset;
    Runnable pendingResetTask;
    Function<Consumer<Double>, Future<?>> scheduleSizeResetOn;
    Function<Double, Function<Consumer<Double>, Future<?>>> sizeReset = q -> p -> {
        if (pendingResetTask != null)
            pendingResetTask.run();

        pendingResetTask = resetTask.apply(q).apply(p);
        if (pendingReset != null && !pendingReset.isDone()) {
            try {
                System.out.println("cancelling size reset sooner");
                pendingReset.cancel(true);
                pendingResetTask.run();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        System.out.println("will schedule size reset");
        return pendingReset = Executors.newSingleThreadScheduledExecutor()
                .schedule(() -> {
                    pendingResetTask.run();
                    letterIndex.set(0);
                    pendingResetTask = null;
                    Platform.runLater(() -> lettersContainer.addText(String.valueOf(currentChar)));
                }, 2100, TimeUnit.MILLISECONDS);
    };

    char getCurrentButtonCharacter(EActionButton eActionButton) {
        System.out.println("getCurrentButtonCharacter: " + letterIndex);
        String label = activeButtonGroup.getLetterForButton(eActionButton);
        System.out.println("label: " + label);
        int i = 0;
        if (letterIndex.get() == 3)
            letterIndex.set(1);
        else
            i = letterIndex.getAndIncrement();
        System.out.println("index: " + i);
        char charAt = label.charAt(i);

        System.out.println("got character: " + String.valueOf(charAt));

        return charAt;
    }

    String str(List<String> lst) {
        return String.join("", lst);
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

        getGroupWidget().selectSegment(groupActiveIndex = index);

        return index + 1;
    }

    @Override
    public void toggleVisual(EActionButton index) {
        activeButtonGroup.toggleButtonVisual(index);
    }

    private EActionButton precisionInitiatior;
    public void activatePrecisionMode(EActionButton eActionButton) {
        System.out.println("activatePrecisionMode");
        char charAt = getCurrentButtonCharacter(precisionInitiatior = eActionButton);
        currentChar = activeButtonGroup.getAssignedTrieKey(eActionButton);

        Consumer<Double> fontSizeSetter = activeButtonGroup.getLettersMap().get(eActionButton)
                .get(charAt);
        scheduleSizeResetOn.apply(fontSizeSetter);

        System.out.println("setting font size to " + fontSizeSetter);
        fontSizeSetter.accept(42D);

    }

    StringBuilder key = new StringBuilder();

    @Override
    public void setActiveAndType(EActionButton index) {
        this.toggleVisual(index);

        if (index != precisionInitiatior) {
            precisionInitiatior = index;
            letterIndex.set(0);
            char prevChar = currentChar;
            Platform.runLater(() -> lettersContainer.addText(String.valueOf(prevChar)));
        }

        if (pendingResetTask == null) {
            System.out.println("letter index 0");
            currentChar = activeButtonGroup.getAssignedTrieKey(index);

            Platform.runLater(() -> {
                wordsContainer.clear();
                predictions = new LinkedList<>(predictor.apply(key.toString()));

                List<String> limitedPredictions = filterWordsByCharLimit(predictions, fittingCharacters);
                long count = limitedPredictions.stream().mapToInt(String::length).sum();
                System.out.println("letter count: " + count);

                predictions.removeAll(limitedPredictions);

                System.out.println("Predictions: " + limitedPredictions);
                setWordsAvailable(limitedPredictions);
            });
        } else {
            System.out.println("precision mode");
            currentChar = getCurrentButtonCharacter(index);
            Consumer<Double> fontSetter = activeButtonGroup.getLettersMap().get(index)
                    .get(currentChar);
            scheduleSizeResetOn.apply(fontSetter);
            fontSetter.accept(42D);

        }

        key.append(currentChar);
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
