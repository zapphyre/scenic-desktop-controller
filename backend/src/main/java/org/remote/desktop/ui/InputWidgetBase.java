package org.remote.desktop.ui;

import com.arun.trie.base.Trie;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.mapstruct.ap.internal.util.Strings;
import org.remote.desktop.model.dto.LanguageDto;
import org.remote.desktop.ui.component.TextContainer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.remote.desktop.ui.CircleButtonsInputWidget.filterWordsByCharLimit;
import static org.remote.desktop.util.FluxUtil.funky;
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

    private final Supplier<List<LanguageDto>> languages;
    protected final Function<Long, Trie<String>> trieGetter;
    private final Function<Long, Consumer<String>> forLangFreqAdjuster;
    protected Consumer<String> currentFrequencyPropper;

    protected boolean persistentPreciseInput;
    double scaleFactor = 1.5;

    private Stage primaryStage;
    protected int fittingCharacters;
    protected TextField sentence;
    //    Text inputMode = new Text("asdf");
    private ToggleButton inputMode;
    private ComboBox<LanguageDto> languagesComboBox;
    protected Trie<String> trie;

    protected Text currPage, slash, allPages;

    protected final TextContainer wordsContainer = new TextContainer();

    abstract Pane createLeftWidget();

    abstract Pane createRightWidget();

    abstract void persistentInputChange(Boolean persistent);

    Function<Boolean, String> precisionToggleText = q -> q ? "Persistent" : "Timely";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        inputMode = new ToggleButton("Input");
        languagesComboBox = new ComboBox<>();
        languagesComboBox.setPrefWidth(69);
        languagesComboBox.getItems().addAll(languages.get());

        languagesComboBox.setCellFactory(listView -> new ListCell<LanguageDto>() {
            @Override
            protected void updateItem(LanguageDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getCode());
            }
        });

        languagesComboBox.valueProperty().addListener((ov, t, t1) -> {
                    languagesComboBox.setValue(t1);
                    trie = trieGetter.apply(t1.getId());
                    currentFrequencyPropper = forLangFreqAdjuster.apply(t1.getId());
                }
        );

        languagesComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(LanguageDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getCode());
            }
        });
        languagesComboBox.getSelectionModel().select(0);


        HBox horiz = new HBox();
        VBox vert = new VBox();
        vert.setAlignment(Pos.CENTER);

        VBox settings = new VBox(inputMode, languagesComboBox);
        HBox hBox = new HBox(settings);
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

        currPage = slash = allPages = new Text("0");
        slash = new Text("/");
        allPages = new Text("0");

        HBox pagingInfo = new HBox();
        pagingInfo.setAlignment(Pos.CENTER_RIGHT);

        HBox wordsLayout = createContentLayout(secondaryTextHeight, scaleFactor);
        HBox lettersLayout = createContentLayout(secondaryTextHeight, scaleFactor);
        sentence = new TextField();
        HBox.setHgrow(wordsContainer, Priority.ALWAYS); // Grow to fill HBox width
        HBox.setHgrow(sentence, Priority.ALWAYS); // Grow to fill HBox width

        Stream.of(currPage, slash, allPages)
                .map(funky(p -> p.setStroke(Paint.valueOf("white"))))
                .forEach(pagingInfo.getChildren()::add);

//        pagingInfo.getChildren().addAll(currPage, slash, currPage);

        wordsLayout.getChildren().addAll(wordsContainer);
        lettersLayout.getChildren().addAll(sentence);

        sentence.setFont(Font.font(32));
        sentence.setBackground(Background.EMPTY);

        textLayouts.getChildren().addAll(pagingInfo, wordsLayout, lettersLayout);
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
        resetPageIndicators();
        wordIdx.set(0);
    }

    protected void resetPageIndicators() {
        currPage.setText("-");
        allPages.setText("-");
        pageIdx.set(1);
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
            if (Strings.isNotEmpty(sentence.getText()))
                sentence.appendText(" ");

            if (wordIdx.get() == 0 && limitedPredictions.isEmpty())
                sentence.appendText(" ");
            else
                Stream.of(wordIdx)
                        .map(AtomicInteger::get)
                        .map(limitedPredictions::get)
                        .peek(sentence::appendText)
                        .forEach(currentFrequencyPropper);

            resetStateClean();
        });
    }

    AtomicInteger wordIdx = new AtomicInteger();
    AtomicInteger pageIdx = new AtomicInteger();
    protected List<String> predictions = new LinkedList<>();
    List<List<String>> predictionsHistory = new LinkedList<>(); // acts like a stack (use add/removeLast)
    protected List<String> limitedPredictions = new LinkedList<>();

    public void nextPredictionsFrame() {
        System.out.println("going forward");
        if (predictions.isEmpty())
            return;

        // Save current frame before moving forward
        if (!limitedPredictions.isEmpty())
            predictionsHistory.add(new ArrayList<>(limitedPredictions));

        // Get next frame
        limitedPredictions = filterWordsByCharLimit(predictions, fittingCharacters);

        // Remove from predictions list
        predictions.removeAll(limitedPredictions);
        wordIdx.set(0);

        currPage.setText(pageIdx.incrementAndGet() + 1 + "");
        setWordsAvailable(limitedPredictions);
    }

    public void prevPredictionsFrame() {
        if (predictionsHistory.isEmpty())
            return;

        System.out.println("going backward");

        // Put current frame back to predictions
        if (!limitedPredictions.isEmpty())
            predictions.addAll(0, limitedPredictions);

        // Load the previous frame
        limitedPredictions = predictionsHistory.removeLast();

        wordIdx.set(0);
        currPage.setText(pageIdx.decrementAndGet() + 1 + "");

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
        String text = sentence.getText();
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
        sentence.backward();
    }

    public void moveSentenceCursorRight() {
        sentence.forward();
    }

    public void moveCursorLeft() {
        Platform.runLater(() -> activeRowControls.left.run());
    }

    public void moveCursorRight() {
        Platform.runLater(() -> activeRowControls.right.run());
    }

    public void clearAllWords() {
        Platform.runLater(wordsContainer::clear);
    }

    public void clearAllLetters() {
        Platform.runLater(() -> sentence.clear());
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
            sentence.requestFocus();
            sentence.positionCaret(sentence.getText().length());
        });
        activeRowControls = new RowControls(this::moveSentenceCursorLeft, this::moveSentenceCursorRight);
    }

    public void moveCursorWordLeft() {
        int previousWordStart = findPreviousWordStart(sentence.getText(), sentence.getCaretPosition());
        sentence.positionCaret(previousWordStart);
    }

    public void moveCursorWordRight() {
        int nextWordStart = findNextWordStart(sentence.getText(), sentence.getCaretPosition());
        sentence.positionCaret(nextWordStart);
    }

    @Value
    @RequiredArgsConstructor
    static class RowControls {
        Runnable left;
        Runnable right;
    }
}
