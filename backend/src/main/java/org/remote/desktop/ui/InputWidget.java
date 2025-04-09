package org.remote.desktop.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class InputWidget extends Application {

    private final double letterSize;
    private final Color arcDefaultFillColor;
    private final double arcDefaultAlpha;
    private final Color highlightedColor;
    private final Color textColor;
    private final int letterGroupCount;

    private Pane root;
    private double rotationAngle;
    private CircleWidgetOld circleWidgetOldLeft;
    private CircleWidgetOld circleWidgetRight;
    private String[] letterGroups;
    private StringBuilder middleText;
    private Text middleTextNode;
    private Rectangle textBackground;

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
        double paddingBelowCircles = 20 * scaleFactor;

        root = new Pane();
        middleText = new StringBuilder();

        circleWidgetOldLeft = new CircleWidgetOld(letterSize, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, 1);
        circleWidgetRight = new CircleWidgetOld(letterSize + 1, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, 0.2);

        letterGroups = splitIntoGroups(alphabet, 4);

        // Compute widget width (left circle + gap + right circle)
        double offsetX = 2 * outerRadius * scaleFactor + 30 * scaleFactor; // Gap between circles
        double widgetWidth = (2 * outerRadius * scaleFactor) + offsetX; // Full widget width
        double sceneWidth = computeSceneWidth(scaleFactor, outerRadius); // Scene width
        double margin = (sceneWidth - widgetWidth) / 2; // Equal margin on both sides

        // Left circle: Shift right by margin
        Scene leftScene = circleWidgetOldLeft.createScene(scaleFactor, highlightSection, innerRadius, outerRadius, letterGroups, 116);
        Pane leftPane = (Pane) leftScene.getRoot();
        leftPane.setTranslateX(margin); // Center the widget
        root.getChildren().add(leftPane);

        String[] letters = arraize(letterGroups[0]);

        // Right circle: Shift right by margin + offsetX
        Scene rightScene = circleWidgetRight.createScene(scaleFactor, highlightSection, innerRadius, outerRadius, letters, 135);
        Pane rightPane = (Pane) rightScene.getRoot();
        rightPane.setTranslateX(margin + offsetX);
        root.getChildren().add(rightPane);

        // Middle text UI component
        middleTextNode = new Text();
        middleTextNode.setFill(textColor);
        middleTextNode.setScaleX(4 * scaleFactor);
        middleTextNode.setScaleY(4 * scaleFactor);
        middleTextNode.setText(middleText.toString());

        // Rectangle background: Span from left circle's outer frame to right circle's outer frame
        textBackground = new Rectangle();
        textBackground.setFill(Color.color(1, 1, 1, 0.5)); // White with alpha 0.5
        textBackground.setWidth(widgetWidth);
        double textHeight = middleTextNode.getBoundsInLocal().getHeight() * 4; // Scaled text height
        textBackground.setHeight(textHeight + 10 * scaleFactor); // Height fits text + padding

        // Position: Below circles with padding, centered across full widget
        double middleX = sceneWidth / 2; // Midpoint of the scene's x-axis
        double middleY = 200 * scaleFactor + paddingBelowCircles + textHeight / 2;
        middleTextNode.setX(middleX - middleTextNode.getBoundsInLocal().getWidth() / 2); // Center text
        middleTextNode.setY(middleY);
        textBackground.setX(middleX - widgetWidth / 2); // Center rectangle, starting from left circle's outer frame
        textBackground.setY(middleY - textBackground.getHeight() / 2);

        root.getChildren().add(textBackground);
        root.getChildren().add(middleTextNode);

        // Scene with refactored dimensions
        Scene combinedScene = new Scene(root, sceneWidth,
                computeSceneHeight(scaleFactor, outerRadius, paddingBelowCircles, textHeight));
        combinedScene.setFill(null);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(combinedScene);
        primaryStage.show();
    }

    // Refactored scene width computation
    private double computeSceneWidth(double scaleFactor, double outerRadius) {
        return 2 * (2 * outerRadius * scaleFactor + 50 * scaleFactor);
    }

    // Refactored scene height computation
    private double computeSceneHeight(double scaleFactor, double outerRadius, double padding, double textHeight) {
        return 200 * scaleFactor + padding + textHeight + 10 * scaleFactor; // Extra padding for rectangle
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

    // Add a letter to the middle text
    public void addLetter(String letter) {
        if (letter == null || letter.isEmpty()) {
            return;
        }
        middleText.append(letter);
        updateMiddleText();
    }

    // Clear the middle text
    public void clearText() {
        middleText.setLength(0);
        updateMiddleText();
    }

    // Update the middle text UI component and background
    private void updateMiddleText() {
        Platform.runLater(() -> {
            middleTextNode.setText(middleText.toString());
            double textWidth = middleTextNode.getBoundsInLocal().getWidth() * 4; // Scaled width
            double textHeight = middleTextNode.getBoundsInLocal().getHeight() * 4; // Scaled height
            textBackground.setHeight(textHeight + 10 * 2); // Height fits text + padding, scaleFactor = 2

            // Recenter horizontally across full widget
            double sceneWidth = computeSceneWidth(2, 90); // Hardcoded scaleFactor = 2, outerRadius = 90
            double middleX = sceneWidth / 2;
            middleTextNode.setX(middleX - textWidth / 2);
            double widgetWidth = (2 * 90 * 2) + (2 * 90 * 2 + 30 * 2); // Full widget width
            textBackground.setX(middleX - widgetWidth / 2);
        });
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
            String[] currentGroups = letterGroups;
            circleWidgetOldLeft.updateSlicesAndLabels(currentGroups, -1);
            circleWidgetRight.updateSlicesAndLabels(currentGroups, -1);
        });
    }

    static String[] getLetterGroups(int numberOfGroups) {
        if (numberOfGroups <= 0 || numberOfGroups > 26) {
            throw new IllegalArgumentException("Number of groups must be between 1 and 26");
        }

        int lettersPerGroup = alphabet.length() / numberOfGroups;
        int remainder = alphabet.length() % numberOfGroups;

        String[] groups = new String[numberOfGroups];
        int letterIndex = 0;

        for (int i = 0; i < numberOfGroups; i++) {
            int groupSize = lettersPerGroup + (i < remainder ? 1 : 0);
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