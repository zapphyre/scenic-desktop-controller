package org.remote.desktop.ui;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.remote.desktop.model.UiButtonBase;
import org.remote.desktop.ui.component.FourButtonWidget;
import org.remote.desktop.ui.model.ButtonInputProcessor;
import org.remote.desktop.ui.model.ButtonsSettings;
import org.remote.desktop.ui.model.EActionButton;
import org.remote.desktop.ui.model.IndexLetterAction;
import org.remote.desktop.util.IdxWordTx;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.remote.desktop.util.KeyboardLayoutTrieUtil.FUNCTION_GROUP_IDX;
import static org.remote.desktop.util.KeyboardLayoutTrieUtil.buttonDict;

public class CircleButtonsInputWidget extends VariableGroupingInputWidgetBase implements ButtonInputProcessor {

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

        groupWidgetMap = buttonDict.keySet().stream() // key is 'group' from 'TrieGroupDef'
                .collect(Collectors.toMap(Function.identity(), q -> { // group too
                    Map<EActionButton, UiButtonBase> groupDefs = buttonDict.get(q);
                    Map<EActionButton, ButtonsSettings> settingsMap = Arrays.stream(EActionButton.values())
                            .map(b -> groupDefs.getOrDefault(b, null))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toMap(UiButtonBase::getButton, a -> bs
//                                    .trieKey(a.getTrieCode())
                                    .charCount(a.getElements().size())
                                    .uiButton(a)
                                    .build()
                            ));

                    return new FourButtonWidget(settingsMap, (widgetSize * 2) * scaleFactor, 24);
                }, (p, q) -> q));

        rightPane.getChildren().add(activeButtonGroup = groupWidgetMap.get(groupActiveIndex = 0));
        scheduleSizeResetOn = sizeReset.apply(activeButtonGroup.getTextSize());
    }

    private final Function<Double, Function<Consumer<Double>, Runnable>> resetTask =
            q -> p -> () -> p.accept(q);

    IndexLetterAction groupTxFun;
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
                    pendingResetTask = null;
//                    WordGenFun wordGenFun = groupTxFun.transforIdxWord(letterIndex.getAndSet(0) - 1);
//                    Platform.runLater(() -> wordGenFun.transform(lettersContainer));
                    Platform.runLater(() -> groupTxFun.actOnIndexLetter(letterIndex.getAndSet(0) - 1));

                }, 2100, TimeUnit.MILLISECONDS);
    };

    IndexLetterAction getCurrentButtonWordTransformationFun(EActionButton eActionButton) {
        return activeButtonGroup.getUiButtonBehaviourDef(eActionButton).processTouch(this);
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
//        groupTxFun = getCurrentButtonWordTransformationFun(precisionInitiatior = eActionButton);
//        groupTxFun = ReplacingUiButtonAdapter.builder()
//                .buttonTouch(activeButtonGroup.getUiButtonBehaviourDef(precisionInitiatior = eActionButton))
//                .build()
//                .processTouch(this);

        groupTxFun = activeButtonGroup.getUiButtonBehaviourDef(precisionInitiatior = eActionButton)
                        .getLongTouchHandler().processTouch(this);

        letterIndex.set(0);
        Consumer<Double> fontSizeSetter = activeButtonGroup.getLettersMap().get(eActionButton)
                .get(letterIndex.getAndIncrement());
        scheduleSizeResetOn.apply(fontSizeSetter);

        System.out.println("setting font size to " + fontSizeSetter);
        fontSizeSetter.accept(42D);

    }

    @Override
    public void setActiveAndType(EActionButton buttonActivated) {
        this.toggleVisual(buttonActivated);
        System.out.println("setActiveAndType");

        if (buttonActivated != precisionInitiatior && pendingResetTask != null) {
//            ILA wordGenFun = groupTxFun.transforIdxWord(letterIndex.getAndSet(0) - 1);
            System.out.println("precision mode; button change");
            groupTxFun.actOnIndexLetter(letterIndex.getAndSet(0) - 1);
            groupTxFun = activeButtonGroup.getUiButtonBehaviourDef(precisionInitiatior = buttonActivated)
                    .getLongTouchHandler().processTouch(this);

//            Platform.runLater(() -> wordGenFun.transform(lettersContainer));
        }

        if (groupActiveIndex == FUNCTION_GROUP_IDX) {
            letterIndex.set(0);
//            IdxWordTx idxWordTx = getCurrentButtonWordTransformationFun(buttonActivated);
            System.out.println("function case");
            getCurrentButtonWordTransformationFun(buttonActivated)
                    .actOnIndexLetter(0);
//            Platform.runLater(() -> idxWordTx.transforIdxWord(lettersContainer.getCaretPosition())
//                    .transform(lettersContainer)
//            );
        } else if (pendingResetTask == null) {
            System.out.println("normal case");
                getCurrentButtonWordTransformationFun(buttonActivated)
                    .actOnIndexLetter(0);
        } else {
            if (letterIndex.get() > activeButtonGroup.sizeOfActionsAssignedToButton(buttonActivated) - 1)
                letterIndex.set(0);

            System.out.println("precision mode");
//            groupTxFun = getCurrentButtonWordTransformationFun(precisionInitiatior = buttonActivated);
//            groupTxFun = LongPressUiButtonAdapter.builder()
//                    .buttonTouch(activeButtonGroup.getUiButtonBehaviourDef(precisionInitiatior = buttonActivated))
//                    .build()
//                    .processTouch(this);
            int i = letterIndex.getAndIncrement();
            System.out.println("index " + i);
            Consumer<Double> fontSetter = activeButtonGroup.getLettersMap().get(buttonActivated)
                    .get(i); //increment has to be here; just here
            scheduleSizeResetOn.apply(fontSetter);

            fontSetter.accept(42D);
        }
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

    @Override
    public void asTrieChar(char c) {
        key.append(c);
        System.out.println("non pending task");

        Platform.runLater(() -> {
            wordsContainer.clear();
            System.out.println("key: " + key.toString());
            predictions = new LinkedList<>(predictor.apply(key.toString()));

            limitedPredictions = filterWordsByCharLimit(predictions, fittingCharacters);
            long count = limitedPredictions.stream().mapToInt(String::length).sum();
            System.out.println("letter count: " + count);

            predictions.removeAll(limitedPredictions);

            System.out.println("Predictions: " + limitedPredictions);
            setWordsAvailable(limitedPredictions);
        });
    }

    @Override
    public void asLetter(String letter) {
        Platform.runLater(() -> lettersContainer.appendText(letter.toLowerCase()));
    }

    @Override
    public void asFunction(IdxWordTx fx) {
        fx.transforIdxWord(lettersContainer.getCaretPosition()).transform(lettersContainer);
    }

    @Override
    public void asDeletingLong(String letter) {
        Platform.runLater(() -> {
            System.out.println("deleting long");
            lettersContainer.deletePreviousChar();
            System.out.println("appending: " + letter);
            lettersContainer.appendText(letter);
        });
    }
}

