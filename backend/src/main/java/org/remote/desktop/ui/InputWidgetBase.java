package org.remote.desktop.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.ui.component.TextContainer;

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

    double scaleFactor = 2;

    private Stage primaryStage;

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

        Scene scene = new Scene(vert);
        scene.setFill(Color.TRANSPARENT);

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

    public void close() {
        Platform.runLater(() -> primaryStage.hide());
    }

    public void render() {
        Platform.runLater(() -> primaryStage.show());
    }
}
