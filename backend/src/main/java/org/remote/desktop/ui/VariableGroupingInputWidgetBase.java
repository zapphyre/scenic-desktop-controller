package org.remote.desktop.ui;

import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.remote.desktop.ui.component.LetterCircle;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class VariableGroupingInputWidgetBase extends InputWidgetBase {

    @Setter
    private Function<String, List<String>> predictor = List::of;

    @Getter
    private LetterCircle letterCircleLeft;
    @Getter
    private LetterCircle groupWidget;
    private String[] letterGroups = new String[]{"A", "B"};

    private StringBuilder middleText = new StringBuilder();
    private HBox lettersLayout;

    // Bottom row (secondaryText)
    private HBox wordsLayout;
    private final double secondaryTextScale = 2;

    public VariableGroupingInputWidgetBase(double widgetSize, double letterSize, Color arcDefaultFillColor, double arcDefaultAlpha, Color highlightedColor, Color textColor, int letterGroupCount, String title) {
        super(widgetSize, letterSize, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, letterGroupCount, title);
    }

    public boolean isReady() {
        return Objects.nonNull(letterGroups);
    }

    @Override
    Pane createLeftWidget() {
        return groupWidget = new LetterCircle(letterSize, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, 1,
                1.5, -1, 40, widgetSize, letterGroups, 116);
    }

    @Override
    Pane createRightWidget() {
        return letterCircleLeft = new LetterCircle(letterSize, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, 1,
                1.5, -1, 40, widgetSize, letterGroups, 135);
    }

    String[] arraize(String input) {
        return input.chars().mapToObj(ch -> String.valueOf((char) ch)).toArray(String[]::new);
    }

    public int highlightSegmentReturnSize(int i) {
        newLetters = arraize(letterGroups[i]);
        Platform.runLater(() -> {
            letterCircleLeft.selectSegment(i);
            groupWidget.setLetterGroups(newLetters);
        });
        return newLetters.length;
    }

    private String[] newLetters;
    private String currentLetter;

    public String pickLetterAndHighlight(int i) {
        Platform.runLater(() -> groupWidget.selectSegment(i));
        return newLetters != null ? (currentLetter = newLetters[i]) : currentLetter;
    }

    public String getLetterPicked() {
        return currentLetter;
    }

    public String getFullContentClearClose() {
        close();
        String word = middleText.toString();
        clearText();
        return word;
    }

    public String getFullLettersContent() {
        String textContent = lettersContainer.getTextContent();
        System.out.println("textContent: " + textContent);
        return textContent;
    }

    public void addSelectedLetter() {
        Platform.runLater(() -> {
            lettersContainer.addText(currentLetter);
            wordsContainer.clear();
            predictor.apply(getFullLettersContent()).stream()
                    .limit(5)
                    .forEach(this::addSecondaryText);
        });
    }

    public void clearText() {

    }

    public void setSecondaryText(List<String> lines) {
        Platform.runLater(() -> {
            wordsLayout.getChildren().clear();

            double scaleFactor = 2;
            for (String word : lines) {

            }
        });
    }

    public void addSecondaryText(String line) {
        System.out.println("addind: " + line);
        Platform.runLater(() -> wordsContainer.addText(line));
    }

    int prevOn = 0;

    public void setFrameOn(int index) {
        wordsContainer.setTextBorderVisible(prevOn, false);
        wordsContainer.setTextBorderVisible(prevOn = index, true);
    }

    public void updateGroups(String[] newGroups) {
        Platform.runLater(() -> {
//            letterCircleLeft.updateSlicesAndLabels(newGroups, -1);
//            circleWidgetRight.updateSlicesAndLabels(newGroups, -1);
        });
    }

    public void updateHighlight(int section) {
        Platform.runLater(() -> {
            letterCircleLeft.selectSegment(section);
            groupWidget.selectSegment(section);
        });
    }

    public void updateRotation(double rotationAngle) {
        Platform.runLater(() -> {
            String[] currentGroups = letterGroups;
//            letterCircleLeft.updateSlicesAndLabels(currentGroups, -1);
//            circleWidgetRight.updateSlicesAndLabels(currentGroups, -1);
        });
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
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