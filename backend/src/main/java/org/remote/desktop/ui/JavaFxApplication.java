package org.remote.desktop.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.remote.desktop.ui.component.LetterCircle;

public class JavaFxApplication extends Application {

    @Override
    public void init() throws Exception {
        // Start Spring context during JavaFX initialization
    }

    LetterCircle circleWidgetLeft;
    LetterCircle circleWidgetRight;

    @Override
    public void start(Stage primaryStage) throws Exception {
     }

    // Method to cycle through different group counts
    private void cycleGroups() {
        new Thread(() -> {
            try {
                int[] groupCounts = {8, 6, 3}; // Cycle through 8, 6, 3 groups
                while (true) {
                    for (int count : groupCounts) {
                        String[] newGroups = getLetterGroups(count);
                        Platform.runLater(() -> {
                            circleWidgetLeft.updateSlicesAndLabels(newGroups, 2);
                            circleWidgetRight.updateSlicesAndLabels(newGroups, 3);
                        });
                        Thread.sleep(2000); // 2-second delay between group changes
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void stop() throws Exception {
        // Clean up Spring context when JavaFX closes
    }

    public static void main(String[] args) {
        // Launch JavaFX app
        launch(args);
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