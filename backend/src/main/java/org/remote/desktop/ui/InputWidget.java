package org.remote.desktop.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class InputWidget extends Application {

    private final double letterSize;
    private final Color arcDefaultFillColor;
    private final double arcDefaultAlpha;
    private final Color highlightedColor;
    private final Color textColor;
    private final int letterGroupCount;
    private final String title;

    private Pane root;
    private CircleWidgetOld circleWidgetOldLeft;
    private CircleWidgetOld circleWidgetRight;
    private String[] letterGroups;
    private Stage primaryStage;

    // Scene dimensions
    private double sceneWidth;
    private double middleX;
    private double widgetWidth;

    // Top row (middleText)
    private StringBuilder middleText;
//    private Rectangle textBackground;
    private final double middleTextScale = 4;
    private final double middleShiftCoefficient = 0.2;
    private HBox lettersLayout;

    // Bottom row (secondaryText)
    private HBox wordsLayout;
    private final double secondaryTextScale = 2;
    private final double secondaryShiftCoefficient = 0.15;

    public static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    TextContainer wordsContainer = new TextContainer();
    TextContainer lettersContainer = new TextContainer();

    public class TextItem extends StackPane {
        private final Text textNode;

        public TextItem(String text) {
            textNode = new Text(text);
            textNode.setFont(Font.font(32));
//            TextFlow flow = new TextFlow(textNode);
//            this.getChildren().add(flow);
            this.getChildren().add(textNode);

            setBorderVisible(false);
        }

        public void setText(String text) {
            textNode.setText(text);
        }

        public String getText() {
            return textNode.getText();
        }

        public void setTextColor(Color color) {
            textNode.setFill(color);
        }

        public void setFont(javafx.scene.text.Font font) {
            textNode.setFont(font);
        }

        public void setBorderVisible(boolean visible) {
            if (visible) {
                this.setBorder(new Border(new BorderStroke(
                        Color.BURLYWOOD,
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(5),
                        new BorderWidths(4)
                )));
            } else {
                this.setBorder(Border.EMPTY);
            }
        }
    }

    public class TextContainer extends HBox {
        private final List<TextItem> items = new ArrayList<>();

        public TextContainer() {
            setSpacing(10);
            setPadding(new javafx.geometry.Insets(10));
        }

        public void addText(String text) {
            TextItem item = new TextItem(text);
            items.add(item);
            this.getChildren().add(item);
        }

        public String getTextContent() {
            return this.items.stream().map(TextItem::getText).collect(Collectors.joining(""));
        }

        public void setTextBorderVisible(int index, boolean visible) {
            if (index >= 0 && index < items.size()) {
                items.get(index).setBorderVisible(visible);
            }
        }

        public void setContainerBackground(Color color) {
            this.setBackground(new Background(new BackgroundFill(
                    color,
                    CornerRadii.EMPTY,
                    javafx.geometry.Insets.EMPTY
            )));
        }
    }

    public boolean isReady() {
        return Objects.nonNull(letterGroups);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        double scaleFactor = 2;
        int highlightSection = 4;
        double innerRadius = 40;
        double outerRadius = 90;
        double paddingBelowCircles = 20 * scaleFactor;
        double paddingBetweenTextFields = 10 * scaleFactor;

        root = new Pane();
        middleText = new StringBuilder();

        circleWidgetOldLeft = new CircleWidgetOld(letterSize, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, 1);
        circleWidgetRight = new CircleWidgetOld(letterSize + 1, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, 0.2);

        letterGroups = splitIntoGroups(alphabet, 4);

        double offsetX = 2 * outerRadius * scaleFactor + 30 * scaleFactor;
        widgetWidth = (2 * outerRadius * scaleFactor) + offsetX;
        sceneWidth = computeSceneWidth(scaleFactor, outerRadius);
        middleX = sceneWidth / 2;
        double margin = (sceneWidth - widgetWidth) / 2;

        Scene leftScene = circleWidgetOldLeft.createScene(scaleFactor, highlightSection, innerRadius, outerRadius, letterGroups, 116);
        Pane leftPane = (Pane) leftScene.getRoot();
        leftPane.setTranslateX(margin);
        root.getChildren().add(leftPane);

        String[] letters = arraize(letterGroups[0]);

        Scene rightScene = circleWidgetRight.createScene(scaleFactor, highlightSection, innerRadius, outerRadius, letters, 135);
        Pane rightPane = (Pane) rightScene.getRoot();
        rightPane.setTranslateX(margin + offsetX);
        root.getChildren().add(rightPane);

        int textHeight = 32;

        // Initialize bottom row (secondaryText) with HBox
        double secondaryTextHeight = new Text("Sample").getBoundsInLocal().getHeight() * secondaryTextScale;
//        double secondaryY = middleY + (middleTextHeight / 2) + (secondaryTextHeight / 2) + paddingBetweenTextFields;

        wordsLayout = createContentLayout(widgetWidth, secondaryTextHeight, leftPane.getHeight(), scaleFactor);
        lettersLayout = createContentLayout(widgetWidth, secondaryTextHeight, wordsLayout.getLayoutY() + wordsLayout.getPrefHeight() + 3, scaleFactor);

        wordsLayout.getChildren().addAll(wordsContainer);
        lettersLayout.getChildren().addAll(lettersContainer);


        root.getChildren().add(lettersLayout);
        root.getChildren().add(wordsLayout);

        Scene combinedScene = new Scene(root, sceneWidth,
                computeSceneHeight(scaleFactor, outerRadius, paddingBelowCircles, textHeight, secondaryTextHeight, paddingBetweenTextFields));
        combinedScene.setFill(null);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(combinedScene);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.hide();
        });
        primaryStage.setTitle("keyboard");
        primaryStage.setAlwaysOnTop(true);
        Platform.setImplicitExit(false);
    }

    HBox createContentLayout(double width, double height, double yOffset, double scaleFactor) {
        HBox layout = new HBox(10 * scaleFactor);
        layout.setAlignment(Pos.CENTER);
        layout.setLayoutX(middleX - widgetWidth / 2);
        layout.setLayoutY(yOffset);
        layout.setPrefWidth(widgetWidth);
        layout.setPrefHeight(height + 20 * scaleFactor);
        layout.setBackground(new Background(new BackgroundFill(Color.color(1, 1, 1, 0.5), null, null)));

        return layout;
    }

    public void close() {
        Platform.runLater(() -> primaryStage.hide());
    }

    public void render() {
        Platform.runLater(() -> primaryStage.show());
    }

    private double computeSceneWidth(double scaleFactor, double outerRadius) {
        return 2 * (2 * outerRadius * scaleFactor + 50 * scaleFactor);
    }

    private double computeSceneHeight(double scaleFactor, double outerRadius, double paddingBelowCircles,
                                      double middleTextHeight, double secondaryTextHeight, double paddingBetweenTextFields) {
        return 200 * scaleFactor + paddingBelowCircles + middleTextHeight + paddingBetweenTextFields + secondaryTextHeight + 10 * scaleFactor;
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
        Platform.runLater(() -> circleWidgetRight.setHighlightedSection(i));
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
        return lettersContainer.getTextContent();
    }

    public void deleteLast() {
        if (middleText.length() > 0) {
            middleText.setLength(middleText.length() - 1);
        }
    }

    public void addSelectedLetter() {
        Platform.runLater(() -> lettersContainer.addText(currentLetter));
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