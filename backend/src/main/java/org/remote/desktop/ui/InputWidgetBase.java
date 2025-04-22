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
import lombok.Setter;
import lombok.Value;
import org.remote.desktop.ui.component.TextContainer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.remote.desktop.ui.CircleButtonsInputWidget.filterWordsByCharLimit;

@RequiredArgsConstructor
public abstract class InputWidgetBase extends Application implements TwoGroupInputWidget {

    protected final double widgetSize;
    protected final double letterSize;
    protected final Color arcDefaultFillColor;
    protected final double arcDefaultAlpha;
    protected final Color highlightedColor;
    protected final Color textColor;
    protected final int letterGroupCount;
    protected final String title;

    double scaleFactor = 1.5;

    private Stage primaryStage;
    protected int fittingCharacters;

    @Setter
    protected Function<String, List<String>> predictor = List::of;

    TextContainer wordsContainer = new TextContainer();
    TextContainer lettersContainer = new TextContainer();

    abstract Pane createLeftWidget();

    abstract Pane createRightWidget();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        HBox horiz = new HBox();
        VBox vert = new VBox(horiz);

        Pane leftWidget = createLeftWidget();
        Pane rightWidget = createRightWidget();

        horiz.getChildren().addAll(leftWidget, rightWidget);

        double secondaryTextHeight = 42; //new Text("Sample").getBoundsInLocal().getHeight() * secondaryTextScale;

        HBox wordsLayout = createContentLayout(secondaryTextHeight, scaleFactor);
        HBox lettersLayout = createContentLayout(secondaryTextHeight, scaleFactor);

        wordsLayout.getChildren().addAll(wordsContainer);
        lettersLayout.getChildren().addAll(lettersContainer);

        vert.getChildren().addAll(wordsLayout, lettersLayout);
        vert.setSpacing(3);
        horiz.setSpacing(7);

        vert.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

        Scene scene = new Scene(vert);
        scene.setFill(Color.TRANSPARENT);

//        fittingCharacters = calculateTextItemsInHBox(horiz);
//        fittingCharacters = calculateTextItemsEmpirically(new HBox(horiz), "q", Font.font(32));
        fittingCharacters = 12;

//        vert.setPrefWidth(widgetSize + 12);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.hide();
        });
        primaryStage.setTitle("keyboard");
        primaryStage.setAlwaysOnTop(true);
        Platform.setImplicitExit(false);
    }

    HBox createContentLayout(double height, double scaleFactor) {
        HBox layout = new HBox(10 * scaleFactor);
        layout.setAlignment(Pos.CENTER);
        layout.setPrefHeight(height + 20 * scaleFactor);
        layout.setBackground(new Background(new BackgroundFill(Color.color(1, 1, 1, 0.5), null, null)));

        return layout;
    }

    @Override
    public void setWordsAvailable(List<String> wordsAvailable) {
        Platform.runLater(() -> wordsAvailable.forEach(text -> wordsContainer.addText(text)));
    }

    public void resetStateClean() {
        clearAllWords();
        resetPredictionStack();
        wordIdx.set(0);
    }

    protected void resetPredictionStack() {

    }

    @Override
    public void addWordToSentence() {
        Platform.runLater(() -> {
            lettersContainer.addText(wordsContainer.getWord(wordIdx.get()));
            resetStateClean();
        });
    }

    AtomicInteger wordIdx = new AtomicInteger(0);
    protected List<String> predictions = new LinkedList<>();

    public void nextPredictionsFrame() {
        List<String> limitedPredictions = filterWordsByCharLimit(predictions, fittingCharacters);

        predictions.removeAll(limitedPredictions);

        System.out.println("Predictions: " + limitedPredictions);
        setWordsAvailable(limitedPredictions);
    }

    private int calculateTextItemsEmpirically(HBox hbox, String textContent, Font font) {
        int count = 0;
        double availableWidth = hbox.getBoundsInLocal().getWidth();

        // Temporarily add Text nodes to check for overflow
        while (true) {
            Text text = new Text(textContent);
            text.setFont(font);
            hbox.getChildren().add(text);

            // Force layout update to ensure accurate bounds
            hbox.layout();

            // Calculate total width of children (including spacing)
            double totalWidth = hbox.getChildren().stream()
                    .mapToDouble(node -> node.getLayoutBounds().getWidth())
                    .sum() + hbox.getSpacing() * (hbox.getChildren().size() - 1);

            if (totalWidth > availableWidth) {
                // Overflow detected, remove the last item
                hbox.getChildren().remove(hbox.getChildren().size() - 1);
                break;
            }

            count++;
        }

        return count;
    }

    protected int calculateTextItemsInHBox(HBox hbox) {
        // Create a sample Text node to measure its width
        Text sampleText = new Text("q");
        sampleText.setFont(Font.font(32));
        hbox.getChildren().add(sampleText);

        // Get the width of the Text node
        double textWidth = sampleText.getLayoutBounds().getWidth();

        // Get the HBox's usable width (subtract padding if any)
        double hboxWidth = hbox.getBoundsInParent().getWidth();

        // Account for spacing between Text items
        double spacing = 0; //hbox.getSpacing();

        // Calculate how many Text items fit
        // Formula: (HBox width) / (Text width + spacing)
        // Use floor to get whole items, and adjust for spacing
        return (int) Math.floor(hboxWidth / (textWidth + spacing));
    }

    @Override
    public String getSentenceAndReset() {
        close();
        clearAllWords();
        clearAllLetters();
        return lettersContainer.getTextContent();
    }

    public void framePreviousPredictedWord() {
        Platform.runLater(() -> wordsContainer.setTextBorderVisible(wordIdx.get()));
        if (wordIdx.get() > 0)
            wordIdx.decrementAndGet();
    }

    public void frameNextPredictedWord() {
        Platform.runLater(() -> wordsContainer.setTextBorderVisible(wordIdx.get()));
        if (wordIdx.get() < wordsContainer.getWordsCount() - 1)
            wordIdx.incrementAndGet();
    }

    public void moveSentenceCursorLeft() {
        System.out.println("moveCursorLeft");
        lettersContainer.getLastItem().moveCursorLeft();
    }

    public void moveSentenceCursorRight() {
        System.out.println("moveCursorRight");
        lettersContainer.getLastItem().moveCursorRight();
    }

    public void moveCursorLeft() {
        Platform.runLater(() -> activeRowControls.left.run());
    }

    public void moveCursorRight() {
        Platform.runLater(() -> activeRowControls.right.run());
    }

    public void clearAllWords() {
        Platform.runLater(() -> wordsContainer.clear());
    }

    public void clearAllLetters() {
        Platform.runLater(() -> lettersContainer.clear());
    }

    public void close() {
        Platform.runLater(() -> primaryStage.hide());
    }

    public void render() {
        Platform.runLater(() -> primaryStage.show());
    }

    private RowControls activeRowControls = new RowControls(this::framePreviousPredictedWord, this::frameNextPredictedWord);
    public void selectTopRow() {
        activeRowControls = new RowControls(this::framePreviousPredictedWord, this::frameNextPredictedWord);
    }

    public void selectBottomRow() {
        activeRowControls = new RowControls(this::moveSentenceCursorLeft, this::moveSentenceCursorRight);
    }

    @Value
    @RequiredArgsConstructor
    static class RowControls {
        Runnable left;
        Runnable right;
    }
}
