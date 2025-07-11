package org.remote.desktop.ui;

import com.arun.trie.base.Trie;
import com.arun.trie.base.ValueFrequency;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.asmus.model.EButtonAxisMapping;
import org.mapstruct.ap.internal.util.Strings;
import org.remote.desktop.model.UiButtonBase;
import org.remote.desktop.model.dto.LanguageDto;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.remote.desktop.util.KeyboardButtonFunctionDefinition.buttonDict;

@Slf4j
public class CircleButtonsInputWidget extends VariableGroupingInputWidgetBase implements ButtonInputProcessor {

    @Getter
    private final Map<Integer, FourButtonWidget> groupWidgetMap;
    private final Consumer<Boolean> persistentPrecisionMode;

    @Getter
    private int groupActiveIndex;
    private final AtomicInteger letterIndex = new AtomicInteger(0);
    private Runnable sceneForce;

    public CircleButtonsInputWidget(double widgetSize, double letterSize, Color arcDefaultFillColor,
                                    double arcDefaultAlpha, Color highlightedColor, Color textColor,
                                    int letterGroupCount, String title,
                                    boolean persistentPreciseInput, Consumer<Boolean> persistentPrecisionMode,
                                    Function<Long, Trie<String>> trieGetter,
                                    Supplier<List<LanguageDto>> languagesGet,
                                    Function<Long, Consumer<String>> langFrqIncrement
    ) {
        super(widgetSize, letterSize, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, title,
                persistentPreciseInput,
                trieGetter,
                languagesGet,
                langFrqIncrement
        );
        this.persistentPrecisionMode = persistentPrecisionMode;

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

    protected void persistentInputChange(Boolean persistent) {
        if (Objects.nonNull(pendingResetTask))
            pendingResetTask.run();

        persistentPrecisionMode.accept(persistent);
    }

    private final Function<Double, Function<Consumer<Double>, Runnable>> resetTask =
            q -> p -> () -> p.accept(q);

    String precisedWord;
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
                log.error("can't cancel letter size changer pending task");
            }
        }

        System.out.println("will schedule size reset");
        return pendingReset = Executors.newSingleThreadScheduledExecutor()
                .schedule(() -> {
                    if (persistentPreciseInput)
                        return;

                    pendingResetTask.run();
                    pendingResetTask = null;

                    // -1 b/c incrementation is post-applied, therefore has to be lowered at the end
                    Platform.runLater(() -> {
                        groupTxFun.actOnIndexLetter(letterIndex.getAndSet(0) - 1);
                        lastSentenceWordDo(w -> precisedWord = w);
                    });

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
            getGroupWidget().selectSegment(groupActiveIndex = index);
        });

        return index + 1;
    }

    @Override
    public void nextPredictionsFrame() {
        if (persistentPreciseInput) { // just end precision input when right trigger engaged
            super.resetStateClean();
            if (pendingResetTask != null)
                pendingResetTask.run();

            pendingResetTask = null;
            persistentPreciseInput = false;
            lastSentenceWordDo(currentFrequencyPropper);
        }

        super.nextPredictionsFrame();
    }

    void lastSentenceWordDo(Consumer<String> wc) {
        String[] s = sentence.getText().split(" ");
        if (s.length > 0)
            wc.accept(s[s.length - 1]);
    }

    @Override
    public void toggleVisual(EActionButton index) {
        this.modifiers = Set.of();
        Platform.runLater(() -> activeButtonGroup.toggleButtonVisual(index));
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
        this.modifiers = modifiers;

        // long press (precision mode) was activated && another button then activation pressed
        if (buttonActivated != precisionInitiatior && pendingResetTask != null) {
            // apply letter on previous buttons position
            if (!persistentPreciseInput)
                groupTxFun.actOnIndexLetter(letterIndex.getAndSet(1) - 1);
            else
                letterIndex.set(1); // if persistentPrecision and another button clicked i need to (re)set idx to 1 (will be -1 on apply)

            // generate transformation function out of new (secondly pressed) button
            groupTxFun = activeButtonGroup.getUiButtonBehaviourDef(precisionInitiatior = buttonActivated)
                    .getLongTouchHandler().processTouch(this);

            Consumer<Double> fontSetter = activeButtonGroup.getLettersMap()
                    .get(buttonActivated).get(0);
            scheduleSizeResetOn.apply(fontSetter);
            fontSetter.accept(42d);

        } else if (pendingResetTask == null) { // default case -- trie input
            getCurrentButtonWordTransformationFun(buttonActivated)
                    .actOnIndexLetter(0);
        } else { // pending task != null -> precision mode was activated before and now the same button is pressed
            if (letterIndex.get() > activeButtonGroup.sizeOfActionsAssignedToButton(buttonActivated) - 1)
                letterIndex.set(0);

            Consumer<Double> fontSetter = activeButtonGroup.getLettersMap()
                    .get(buttonActivated).get(letterIndex.getAndIncrement()); //increment has to be here; just here and in precision activation
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

    void tryPropLastPrecised() {
        if (Strings.isEmpty(precisedWord))
            return;

        currentFrequencyPropper.accept(precisedWord);
        System.out.println("precisedWord: " + precisedWord + " propped");
        precisedWord = "";
    }

    @Override // in case precision mode is active, add current character (speedup)
    public void addWordToSentence() {
        tryPropLastPrecised();

        if (pendingResetTask == null) {
            super.addWordToSentence();
            return;
        }

        groupTxFun.actOnIndexLetter(letterIndex.get() - 1);
    }

    @Override
    public String getSentenceAndReset() {
        tryPropLastPrecised();
        return super.getSentenceAndReset();
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
            predictions = trie.getValueFreqSuggestions(key.toString()).stream()
                    .sorted()
                    .map(ValueFrequency::getValue)
                    .collect(Collectors.toCollection(ArrayList::new));

            pageIdx.set(0);
            currPage.setText("1");
            Executors.newSingleThreadExecutor()
                    .submit(() -> allPages.setText(chunkWordsByCharLimit(predictions, fittingCharacters).size() + 1 + ""));

            if (predictions.isEmpty()) {
                sentence.appendText(" ");
                activatePrecisionMode(EActionButton.Y);
            }

            limitedPredictions = (persistentPreciseInput = predictions.isEmpty()) ?
                    List.of("[no suggestions]") : filterWordsByCharLimit(predictions, fittingCharacters);

            predictions.removeAll(limitedPredictions);

            setWordsAvailable(limitedPredictions);
        });
    }

    public static List<List<String>> chunkWordsByCharLimit(List<String> words, int maxChars) {
        List<List<String>> result = new ArrayList<>();
        List<String> remaining = new ArrayList<>(words);

        while (!remaining.isEmpty()) {
            List<String> chunk = filterWordsByCharLimit(remaining, maxChars);
            if (chunk.isEmpty())
                chunk.add(remaining.getFirst());

            result.add(chunk);
            remaining = remaining.stream().skip(chunk.size()).toList();
        }

        return result;
    }

    @Override
    public void asLetter(String letter) {
        sentence.appendText(letter.toLowerCase());
    }

    @Override
    public void asFunction(IdxWordTx fx) {
        fx.transforIdxWord(sentence.getCaretPosition(), modifiers).transform(sentence);
    }

    @Override
    public void asDeletingLong(String letter) {
        Platform.runLater(() -> {
            sentence.deletePreviousChar();
            sentence.appendText(letter);
        });
    }

    public void setKeyboardSceneActuator(Runnable forceScene) {
        sceneForce = forceScene;
    }
}

