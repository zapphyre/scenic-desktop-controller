package org.remote.desktop.ui.trigger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class TriggerSelectApplication<L, R> extends Application {
    private final List<? extends L> leftItems;
    private final List<? extends R> rightItems;
    private final TriggerSelector<L, R> selector = new TriggerSelector<>();
    private Stage primaryStage;

    private Runnable setCols = () -> {
    };

    public SelectedCallback<L, R> setItems(List<? extends L> leftItems, List<? extends R> rightItems,
                                           Function<? super L, String> leftLabelGetter, Function<? super R, String> rightLabelGetter) {

//        TriggerSelector.SelectedGetter<L, R> getter =
        setCols = () -> selector.setColumns(
                leftItems, rightItems, leftLabelGetter, rightLabelGetter
        );

        return callback ->
                selector.setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case DOWN:
                            selector.selectNext();
                            break;
                        case UP:
                            selector.selectPrevious();
                            break;
                        case LEFT:
                            selector.switchToLeft();
                            break;
                        case RIGHT:
                            selector.switchToRight();
                            break;
                        case ENTER:
                            callback.accept(selector.getSelected());
                            break;
                    }
                });
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        Scene scene = new Scene(selector, 400, 300);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Trigger Selector");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.hide();
        });

        setCols.run();
        selector.requestFocus();
    }

    public void close() {
        Platform.runLater(() -> primaryStage.hide());
    }

    public void render() {
        Platform.runLater(() -> {
            this.primaryStage.requestFocus();
            this.primaryStage.show();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}