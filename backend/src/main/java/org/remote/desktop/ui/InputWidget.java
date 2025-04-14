package org.remote.desktop.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.remote.desktop.ui.component.FourButtonWidget;
import org.remote.desktop.ui.component.LetterCircle;
import org.remote.desktop.ui.component.TextContainer;
import org.remote.desktop.ui.model.ButtonsSettings;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static org.remote.desktop.util.AlphabetUtil.defaultAlphabetGroups;

@RequiredArgsConstructor
public class InputWidget extends Application {

    private final double letterSize;
    private final Color arcDefaultFillColor;
    private final double arcDefaultAlpha;
    private final Color highlightedColor;
    private final Color textColor;
    private final int letterGroupCount;
    private final String title;

    @Setter
    private Function<String, List<String>> predictor = List::of;

    private Pane root;
    private LetterCircle letterCircleLeft;
    private LetterCircle circleWidgetRight;
    private String[] letterGroups;
    private Stage primaryStage;

    private double sceneWidth;
    private double middleX;
    private double widgetWidth;

    private StringBuilder middleText;
    private HBox lettersLayout;

    // Bottom row (secondaryText)
    private HBox wordsLayout;
    private final double secondaryTextScale = 2;

    TextContainer wordsContainer = new TextContainer();
    TextContainer lettersContainer = new TextContainer();

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

        letterCircleLeft = new LetterCircle(letterSize, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, 1);
        circleWidgetRight = new LetterCircle(letterSize + 1, arcDefaultFillColor, arcDefaultAlpha, highlightedColor, textColor, 0.2);

        letterGroups = defaultAlphabetGroups(4);

        double offsetX = 2 * outerRadius * scaleFactor + 30 * scaleFactor;
        widgetWidth = (2 * outerRadius * scaleFactor) + offsetX;
        sceneWidth = computeSceneWidth(scaleFactor, outerRadius);
        middleX = sceneWidth / 2;
        double margin = (sceneWidth - widgetWidth) / 2;

        Scene leftScene = letterCircleLeft.createScene(scaleFactor, highlightSection, innerRadius, outerRadius, letterGroups, 116);
        Pane leftPane = (Pane) leftScene.getRoot();
        leftPane.setTranslateX(margin);
        root.getChildren().add(leftPane);

        String[] letters = arraize(letterGroups[0]);

//        Scene rightScene = circleWidgetRight.createScene(scaleFactor, highlightSection, innerRadius, outerRadius, letters, 135);

        ButtonsSettings bs = ButtonsSettings.builder()
                .baseColor(Color.BURLYWOOD)
                .alpha(.8)
                .textColor(Color.DARKGOLDENROD)
                .letters("A B C D")
                .build();

        FourButtonWidget rightPane = new FourButtonWidget(bs, bs, bs, bs, 300, 24);
//        Pane rightPane = (Pane) rightScene.getRoot();
        rightPane.setTranslateX(margin + offsetX+ 100);
        rightPane.setTranslateY(200);
        root.getChildren().add(rightPane);

        int textHeight = 32;

        // Initialize bottom row (secondaryText) with HBox
        double secondaryTextHeight = new Text("Sample").getBoundsInLocal().getHeight() * secondaryTextScale;

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
            letterCircleLeft.setHighlightedSection(i);
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
        String textContent = lettersContainer.getTextContent();
        System.out.println("textContent: " + textContent);
        return textContent;
    }

    public void deleteLast() {
        if (middleText.length() > 0) {
            middleText.setLength(middleText.length() - 1);
        }
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
            letterCircleLeft.updateSlicesAndLabels(newGroups, -1);
            circleWidgetRight.updateSlicesAndLabels(newGroups, -1);
        });
    }

    public void updateHighlight(int section) {
        Platform.runLater(() -> {
            letterCircleLeft.setHighlightedSection(section);
            circleWidgetRight.setHighlightedSection(section);
        });
    }

    public void updateRotation(double rotationAngle) {
        Platform.runLater(() -> {
            String[] currentGroups = letterGroups;
            letterCircleLeft.updateSlicesAndLabels(currentGroups, -1);
            circleWidgetRight.updateSlicesAndLabels(currentGroups, -1);
        });
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }
}