package org.remote.desktop.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JavaFxApplication extends Application {

    @Override
    public void init() throws Exception {
        // Start Spring context during JavaFX initialization
    }

    CircleWidget circleWidgetLeft;
    CircleWidget circleWidgetRight;

    @Override
    public void start(Stage primaryStage) throws Exception {
        circleWidgetLeft = new CircleWidget();
        circleWidgetRight = new CircleWidget();

        String[] groups = getLetterGroups(6);

        // Parameters for both circles
        double scaleFactor = 2;
        int highlightSection = 4;
        double innerRadius = 40;
        double outerRadius = 90;
        double rotationAngle = 120;

        // Create a root pane to hold both circles
        Pane root = new Pane();

        // Left circle
        Scene leftScene = circleWidgetLeft.createScene(scaleFactor, highlightSection, innerRadius, outerRadius, groups, rotationAngle);
        Pane leftPane = (Pane) leftScene.getRoot();
        root.getChildren().add(leftPane);

        // Right circle (shifted to the right)
        Scene rightScene = circleWidgetRight.createScene(scaleFactor, highlightSection, innerRadius, outerRadius, groups, rotationAngle);
        Pane rightPane = (Pane) rightScene.getRoot();
        double offsetX = 2 * outerRadius * scaleFactor + 50 * scaleFactor; // Outer diameter + padding
        rightPane.setTranslateX(offsetX); // Shift right by the diameter of the left circle plus padding
        root.getChildren().add(rightPane);

        // Create a combined scene with enough width for both circles
        Scene combinedScene = new Scene(root, 2 * (2 * outerRadius * scaleFactor + 80 * scaleFactor), 200 * scaleFactor);
        combinedScene.setFill(null); // Transparent background

        primaryStage.initStyle(StageStyle.TRANSPARENT); // Bezel-less
        primaryStage.setScene(combinedScene);
        primaryStage.show();

        // Start the highlight cycling
        cycleHighlights(groups.length);
    }

    @Override
    public void stop() throws Exception {
        // Clean up Spring context when JavaFX closes
    }

    public static void main(String[] args) {
        // Launch JavaFX app
        launch(args);
    }

    // Method to cycle through highlights with a delay
    private void cycleHighlights(int numberOfGroups) {
        new Thread(() -> {
            try {
                while (true) { // Infinite loop; stop with application close
                    for (int i = 0; i < numberOfGroups; i++) {
                        final int highlight = i;
                        Platform.runLater(() -> {
                            circleWidgetLeft.setHighlightedSection(highlight);
                            circleWidgetRight.setHighlightedSection(highlight);
                        });
                        Thread.sleep(1000); // 1-second delay between highlights
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public static String[] getLetterGroups(int numberOfGroups) {
        if (numberOfGroups <= 0 || numberOfGroups > 26) {
            throw new IllegalArgumentException("Number of groups must be between 1 and 26");
        }

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
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
}