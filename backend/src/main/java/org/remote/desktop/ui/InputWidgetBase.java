package org.remote.desktop.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import org.remote.desktop.ui.component.TextContainer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.remote.desktop.ui.CircleButtonsInputWidget.filterWordsByCharLimit;
import static org.remote.desktop.util.TextUtil.findNextWordStart;
import static org.remote.desktop.util.TextUtil.findPreviousWordStart;

@AllArgsConstructor
@RequiredArgsConstructor
public abstract class InputWidgetBase extends Application implements TwoGroupInputWidget {

    protected final double widgetSize;
    protected final double letterSize;
    protected final Color arcDefaultFillColor;
    protected final double arcDefaultAlpha;
    protected final Color highlightedColor;
    protected final Color textColor;
    private final boolean persistentPreciseInputInit;

    protected boolean persistentPreciseInput;
    double scaleFactor = 1.5;

    private Stage primaryStage;
    protected int fittingCharacters;
    protected TextField lettersContainer;
    //    Text inputMode = new Text("asdf");
    ToggleButton inputMode;

    @Setter
    protected Function<String, List<String>> predictor = List::of;

    protected final TextContainer wordsContainer = new TextContainer();

    abstract Pane createLeftWidget();

    abstract Pane createRightWidget();

    abstract  void persistentInputChange(Boolean persistent);

    Function<Boolean, String> precisionToggleText = q -> q ? "Persistent" : "Timely";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        inputMode = new ToggleButton("Input");

        HBox horiz = new HBox();
        VBox vert = new VBox();
        vert.setAlignment(Pos.CENTER);

        HBox hBox = new HBox(inputMode);
//        hBox.setBackground(Background.fill(Paint.valueOf("yellow")));
        vert.getChildren().add(hBox);

        inputMode.setText(precisionToggleText.apply(persistentPreciseInput = persistentPreciseInputInit));

        inputMode.setOnAction(event -> {
                    persistentInputChange(persistentPreciseInput = !persistentPreciseInput);
                    inputMode.setText(precisionToggleText.apply(persistentPreciseInput));
                }
        );

        VBox textLayouts = new VBox();

        Pane leftWidget = createLeftWidget();
        Pane rightWidget = createRightWidget();

        VBox leftVert = new VBox(leftWidget);
        leftVert.setAlignment(Pos.CENTER);
        horiz.getChildren().add(leftVert);

        double secondaryTextHeight = 42; //new Text("Sample").getBoundsInLocal().getHeight() * secondaryTextScale;

        HBox wordsLayout = createContentLayout(secondaryTextHeight, scaleFactor);
        HBox lettersLayout = createContentLayout(secondaryTextHeight, scaleFactor);
        lettersContainer = new TextField();
        HBox.setHgrow(wordsContainer, Priority.ALWAYS); // Grow to fill HBox width
        HBox.setHgrow(lettersContainer, Priority.ALWAYS); // Grow to fill HBox width

        wordsLayout.getChildren().addAll(wordsContainer);
        lettersLayout.getChildren().addAll(lettersContainer);

        lettersContainer.setFont(Font.font(32));
        lettersContainer.setBackground(Background.EMPTY);

        textLayouts.getChildren().addAll(wordsLayout, lettersLayout);
        textLayouts.setSpacing(3);
        textLayouts.setPrefWidth(690);

        horiz.setSpacing(7);
        horiz.setAlignment(Pos.CENTER);
        HBox.setHgrow(horiz, Priority.ALWAYS); // Grow to fill HBox width

        VBox textVert = new VBox(textLayouts);
        textVert.setAlignment(Pos.CENTER);
        horiz.getChildren().addAll(textVert, rightWidget);
        vert.getChildren().add(horiz);
        vert.setAlignment(Pos.CENTER);
        vert.backgroundProperty().set(Background.EMPTY);

        Scene scene = new Scene(vert);
        scene.setFill(Color.TRANSPARENT);

//        fittingCharacters = calculateTextItemsInHBox(horiz);
//        fittingCharacters = calculateTextItemsEmpirically(new HBox(horiz), "q", Font.font(32));
        fittingCharacters = 28;

        renderOnLowerNthPartOfScreen(primaryStage, 4);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.hide();
        });

        Platform.setImplicitExit(false);
    }

    void renderOnLowerNthPartOfScreen(Stage primaryStage, int fraction) {
        primaryStage.setTitle("keyboard");
        primaryStage.setAlwaysOnTop(true);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        double stageWidth = primaryStage.getWidth();
        double stageHeight = primaryStage.getHeight();
        primaryStage.setOnShown(e -> {
            double x = (bounds.getWidth() - primaryStage.getWidth()) / 2;
            double y = bounds.getHeight() - (bounds.getHeight() / fraction) - (primaryStage.getHeight() / 2);
            primaryStage.setX(x);
            primaryStage.setY(y);
        });
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
        Platform.runLater(() -> wordsContainer.replaceContent(wordsAvailable));
    }

    public void resetStateClean() {
        clearAllWords();
        resetPredictionStack();
        wordIdx.set(0);
    }

    protected StringBuilder key = new StringBuilder();

    protected void resetPredictionStack() {
        predictions.clear();
        limitedPredictions.clear();
        key.setLength(0);
    }

    @Override
    public void addWordToSentence() {
        Platform.runLater(() -> {
            if (!lettersContainer.getText().isEmpty() || !lettersContainer.getText().isBlank())
                lettersContainer.appendText(" ");

            if (wordIdx.get() == 0 && limitedPredictions.isEmpty())
                lettersContainer.appendText(" ");
            else
                lettersContainer.appendText(limitedPredictions.get(wordIdx.get()));

            resetStateClean();
        });
    }

    AtomicInteger wordIdx = new AtomicInteger(0);
    protected List<String> predictions = new LinkedList<>();
    protected List<String> limitedPredictions = new LinkedList<>();

    public void nextPredictionsFrame() {
        limitedPredictions = filterWordsByCharLimit(predictions, fittingCharacters);

        predictions.removeAll(limitedPredictions);
        wordIdx.set(0);

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
        String text = lettersContainer.getText();
        close();
        clearAllWords();
        clearAllLetters();

        System.out.println("will write " + text);
        return text;
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

    static String getLastWord(TextField textField) {
        String[] words = textField.getText().split(" ");
        return words[words.length - 1];
    }

    public void moveSentenceCursorLeft() {
        lettersContainer.backward();
    }

    public void moveSentenceCursorRight() {
        lettersContainer.forward();
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
        Platform.runLater(() -> {
            primaryStage.requestFocus();
            primaryStage.show();
        });
    }

    private RowControls activeRowControls = new RowControls(this::framePreviousPredictedWord, this::frameNextPredictedWord);

    public void selectTopRow() {
        activeRowControls = new RowControls(this::framePreviousPredictedWord, this::frameNextPredictedWord);
    }

    public void selectBottomRow() {
        Platform.runLater(() -> {
            lettersContainer.requestFocus();
            lettersContainer.positionCaret(lettersContainer.getText().length());
        });
        activeRowControls = new RowControls(this::moveSentenceCursorLeft, this::moveSentenceCursorRight);
    }

    public void moveCursorWordLeft() {
        int previousWordStart = findPreviousWordStart(lettersContainer.getText(), lettersContainer.getCaretPosition());
        lettersContainer.positionCaret(previousWordStart);
    }

    public void moveCursorWordRight() {
        int nextWordStart = findNextWordStart(lettersContainer.getText(), lettersContainer.getCaretPosition());
        lettersContainer.positionCaret(nextWordStart);
    }

    @Value
    @RequiredArgsConstructor
    static class RowControls {
        Runnable left;
        Runnable right;
    }
}
