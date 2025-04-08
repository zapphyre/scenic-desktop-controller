package org.remote.desktop.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Objects;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class InputWidget extends Application {

    private final double letterSize;
    private final Color arcDefaultFillColor;
    private final double arcDefaultAlpha;
    private final Color highlightedColor;
    private final javafx.scene.paint.Color textColor;
    private final int letterGroupCount;

    private Pane root;
    private double rotationAngle; // Store for updates

    private CircleWidgetOld circleWidgetOldLeft;
    private CircleWidgetOld circleWidgetRight;
    private String[] letterGroups;

    public static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public boolean isReady() {
        return Objects.nonNull(letterGroups);
    }

    @Override
    public void start(Stage primaryStage) {

        double scaleFactor = 2;
        int highlightSection = 4;
        double innerRadius = 40;
        double outerRadius = 90;
        double rotationAngle = 130;

        Pane root = new Pane();

        circleWidgetOldLeft = new CircleWidgetOld(letterSize, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, 1);
        circleWidgetRight = new CircleWidgetOld(letterSize + 1, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, .2);

//        letterGroups = getLetterGroups(letterGroupCount);
        letterGroups = splitIntoGroups(alphabet, 4);


        // Left circle
        Scene leftScene = circleWidgetOldLeft.createScene(scaleFactor, highlightSection, innerRadius, outerRadius, letterGroups, 116);
        Pane leftPane = (Pane) leftScene.getRoot();
        root.getChildren().add(leftPane);

        String[] letters = arraize(letterGroups[0]);

        // Right circle
        Scene rightScene = circleWidgetRight.createScene(scaleFactor, highlightSection, innerRadius, outerRadius, letters, 135);
        Pane rightPane = (Pane) rightScene.getRoot();
        double offsetX = 2 * outerRadius * scaleFactor + 30 * scaleFactor;
        rightPane.setTranslateX(offsetX);
        root.getChildren().add(rightPane);

        Scene combinedScene = new Scene(root, 2 * (2 * outerRadius * scaleFactor + 50 * scaleFactor), 200 * scaleFactor);
        combinedScene.setFill(null);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(combinedScene);
        primaryStage.show();

        // Start cycling groups
//        cycleGroups();
    }

    String[] arraize(String input) {
        return input.chars().mapToObj(ch -> String.valueOf((char) ch)).toArray(String[]::new);
    }

    public int highlightSegmentReturnSize(int i) {
        newLetters = arraize(letterGroups[i]);
        Platform.runLater(() -> {
            circleWidgetOldLeft.setHighlightedSection(i);
            circleWidgetRight.setLetterGroups(newLetters);
        });

        return newLetters.length;
    }

    private String[] newLetters;
    private String currentLetter;
    public String pickLetterAndHighlight(int i) {
        Platform.runLater(() -> {
            circleWidgetRight.setHighlightedSection(i);
        });

        if (newLetters == null) {
            System.out.println("newLetters is null");
            return currentLetter;
        }

        return currentLetter = newLetters[i];
    }

    public String getLetterPicked() {
        return currentLetter;
    }


    // Instance methods for runtime updates
    public void updateGroups(String[] newGroups) {
        Platform.runLater(() -> {
            circleWidgetOldLeft.updateSlicesAndLabels(newGroups, -1);
            circleWidgetRight.updateSlicesAndLabels(newGroups, -1);
        });
    }

    public void updateHighlight(int section) {
        Platform.runLater(() -> {
            circleWidgetOldLeft.setHighlightedSection(section);
            circleWidgetRight.setHighlightedSection(section);
        });
    }

    public void updateRotation(double rotationAngle) {
        this.rotationAngle = rotationAngle;
        Platform.runLater(() -> {
            String[] currentGroups = {"A", "B", "C", "D"}; // Replace with actual current groups if needed
            circleWidgetOldLeft.updateSlicesAndLabels(currentGroups, -1);
            circleWidgetRight.updateSlicesAndLabels(currentGroups, -1);
        });
    }

    static String[] getLetterGroups(int numberOfGroups) {
        if (numberOfGroups <= 0 || numberOfGroups > 26) {
            throw new IllegalArgumentException("Number of groups must be between 1 and 26");
        }

        int lettersPerGroup = alphabet.length() / numberOfGroups;
        int remainder = alphabet.length() % numberOfGroups; // Extra letters to distribute

        String[] groups = new String[numberOfGroups];
        int letterIndex = 0;

        for (int i = 0; i < numberOfGroups; i++) {
            int groupSize = lettersPerGroup + (i < remainder ? 1 : 0); // Distribute remainder
            StringBuilder group = new StringBuilder();
            for (int j = 0; j < groupSize && letterIndex < alphabet.length(); j++) {
                group.append(alphabet.charAt(letterIndex++));
            }
            groups[i] = group.toString();
        }

        return groups;
    }

    public static String[] splitIntoGroups(String input, int size) {
        if (input == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Group size must be at least 1");
        }

        return IntStream.range(0, (input.length() + size - 1) / size)
                .mapToObj(i -> {
                    int start = i * size;
                    int end = Math.min(start + size, input.length());
                    return input.substring(start, end);
                })
                .toArray(String[]::new);
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }
}