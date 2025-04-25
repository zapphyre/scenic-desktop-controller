package org.remote.desktop.ui;

import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.remote.desktop.ui.component.LetterCircle;
import org.remote.desktop.ui.model.EActionButton;

import java.util.List;
import java.util.Objects;

public class VariableGroupingInputWidgetBase extends InputWidgetBase {

    @Getter
    private LetterCircle letterCircleLeft;

    @Getter
    private LetterCircle groupWidget;
    public static String[] letterGroups = new String[]{"◑", "༶", "◐", "Ⲯ"};

    private StringBuilder middleText = new StringBuilder();
    private HBox lettersLayout;

    public VariableGroupingInputWidgetBase(double widgetSize, double letterSize, Color arcDefaultFillColor, double arcDefaultAlpha, Color highlightedColor, Color textColor, int letterGroupCount, String title) {
        super(widgetSize, letterSize, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, letterGroupCount, title);
    }

    @Override
    Pane createLeftWidget() {
        return groupWidget = new LetterCircle(letterSize, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, 1,
                1, -1, 40, widgetSize, letterGroups, 225);
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
    public void toggleVisual(EActionButton index) {

    }

    @Override
    public int setGroupActive(int index) {
        return 0;
    }

    @Override
    public void setActiveAndType(EActionButton index) {
    }
}