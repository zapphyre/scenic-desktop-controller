package org.remote.desktop.ui.trigger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class TriggerSelectApplication<T> extends Application {

    private final List<? extends UiSelectable<?>> leftItems;
    private final List<? extends UiSelectable<?>> rightItems;
    TriggerSelector<UiSelectable<?>, UiSelectable<?>> selector;

    private Stage primaryStage;

    public <L extends UiSelectable<L>, R extends UiSelectable<R>> Consumer<Map.Entry<L, R>> setItems(List<L> leftItems, List<R> rightItems) {
        selector.setColumns(leftItems, rightItems, UiSelectable::getItemName, UiSelectable::getItemName);
        return q -> {

        };
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        selector = new TriggerSelector<>();

//        selector.setColumns(leftItems, rightItems, UiSelectable::getItemName, UiSelectable::getItemName);

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
                    Map.Entry<UiSelectable<?>, UiSelectable<?>> selected = selector.getSelected();
                    break;
            }
        });

        Scene scene = new Scene(selector, 400, 300);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Trigger Selector");
        primaryStage.setScene(scene);

        selector.requestFocus();
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

    public static void main(String[] args) {
        launch(args);
    }
}