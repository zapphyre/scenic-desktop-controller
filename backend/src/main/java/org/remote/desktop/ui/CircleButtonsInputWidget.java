package org.remote.desktop.ui;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.model.EButtonAxisMapping;
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

import static org.remote.desktop.util.KeyboardButtonFunctionDefinition.buttonDict;

public class CircleButtonsInputWidget extends VariableGroupingInputWidgetBase implements ButtonInputProcessor {

    @Getter
    private final Map<Integer, FourButtonWidget> groupWidgetMap;

    @Getter
    private int groupActiveIndex;
    AtomicInteger letterIndex = new AtomicInteger(0);
    private Runnable sceneForce;

    public CircleButtonsInputWidget(double widgetSize, double letterSize, Color arcDefaultFillColor, double arcDefaultAlpha, Color highlightedColor, Color textColor, int letterGroupCount, String title, Consumer<String> importantor) {
        super(widgetSize, letterSize, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, title, importantor);
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
                                    .charCount(a.getLettersOnButton().size())
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
                    // -1 b/c incrementation is post-applied, therefore has to be lowered at the end
                    Platform.runLater(() -> groupTxFun.actOnIndexLetter(letterIndex.getAndSet(0) - 1));

                    if (Objects.nonNull(sceneForce))
                        sceneForce.run();

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
        this.modifiers = Set.of();
        activeButtonGroup.toggleButtonVisual(index);
    }

    private EActionButton precisionInitiatior;

    public void activatePrecisionMode(EActionButton eActionButton) {
        // button long-pressed; will get longTouchHandler out of current uiButton definition
        groupTxFun = activeButtonGroup.getUiButtonBehaviourDef(precisionInitiatior = eActionButton)
                .getLongTouchHandler().processTouch(this);

        letterIndex.set(1); //start fresh
        Consumer<Double> fontSizeSetter = activeButtonGroup.getLettersMap().get(eActionButton)
                .get(0); // get 0 and increment so on next touch idx + 1 is ready
        scheduleSizeResetOn.apply(fontSizeSetter);

        fontSizeSetter.accept(42d);
    }

    Set<EButtonAxisMapping> modifiers = Set.of();

    @Override
    public void setActiveAndType(EActionButton buttonActivated, Set<EButtonAxisMapping> modifiers) {
        this.toggleVisual(buttonActivated);
        this.modifiers = modifiers;

        // long press (precision mode) was activated && another button then activation pressed
        if (buttonActivated != precisionInitiatior && pendingResetTask != null) {
            // apply letter on previous buttons position
            groupTxFun.actOnIndexLetter(letterIndex.getAndSet(1) - 1);

            // generate transformation function out of new (secondly pressed) button
            groupTxFun = activeButtonGroup.getUiButtonBehaviourDef(precisionInitiatior = buttonActivated)
                    .getLongTouchHandler().processTouch(this);

            Consumer<Double> fontSetter = activeButtonGroup.getLettersMap().get(buttonActivated)
                    .get(0);
            scheduleSizeResetOn.apply(fontSetter);
            fontSetter.accept(42d);

        } else if (pendingResetTask == null) { // default case -- trie input
            getCurrentButtonWordTransformationFun(buttonActivated)
                    .actOnIndexLetter(0);
        } else { // pending task != null -> precision mode was activated before and now the same button is pressed
            if (letterIndex.get() > activeButtonGroup.sizeOfActionsAssignedToButton(buttonActivated) - 1)
                letterIndex.set(0);

            Consumer<Double> fontSetter = activeButtonGroup.getLettersMap().get(buttonActivated)
                    .get(letterIndex.getAndIncrement()); //increment has to be here; just here and in precision activation
            scheduleSizeResetOn.apply(fontSetter);

            fontSetter.accept(42d);
        }
    }

    @Override
    public void resetStateClean() {
        if (pendingResetTask == null)
            super.resetStateClean();
        else
            asDeletingLong("");
    }

    @Override // in case precision mode is active, add current character (speedup)
    public void addWordToSentence() {
        if (pendingResetTask == null) {
            super.addWordToSentence();
            return;
        }

        groupTxFun.actOnIndexLetter(letterIndex.get() - 1);
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
            // + (result.isEmpty() ? 0 : 1);

            // Check if adding the word exceeds the max character limit
            if (currentCharCount + wordLength <= maxChars) {
                result.add(word);
                currentCharCount += wordLength;
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

        Platform.runLater(() -> {
            wordsContainer.clear();
            predictions = new LinkedList<>(predictor.apply(key.toString()));

            limitedPredictions = filterWordsByCharLimit(predictions, fittingCharacters);
//            long count = limitedPredictions.stream().mapToInt(String::length).sum();

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
        fx.transforIdxWord(lettersContainer.getCaretPosition(), modifiers).transform(lettersContainer);
    }

    @Override
    public void asDeletingLong(String letter) {
        Platform.runLater(() -> {
            lettersContainer.deletePreviousChar();
            lettersContainer.appendText(letter);
        });
    }

    public void setKeyboardSceneActuator(Runnable forceScene) {
        sceneForce = forceScene;
    }

    @RequiredArgsConstructor
    static abstract class ButtonAction {
        int idx = 0;
        UiButtonBase buttonBase;
        List<String> letters;
    }

    class ButtonLetter extends ButtonAction {

    }
}

