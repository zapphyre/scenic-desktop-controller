package org.remote.desktop.ui.trigger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TriggerSelectApplication extends Application {

    private final List<? extends UiSelectable<?>> leftItems;
    private final List<? extends UiSelectable<?>> rightItems;

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        TriggerSelector<UiSelectable<?>, UiSelectable<?>> selector = new TriggerSelector<>();

        selector.setColumns(leftItems, rightItems, UiSelectable::getItemName, UiSelectable::getItemName);

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
            }
        });

        Scene scene = new Scene(selector, 400, 300);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        (this.primaryStage = primaryStage).initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Trigger Selector");
        primaryStage.setScene(scene);

        Platform.runLater(() -> {
            primaryStage.requestFocus();
            primaryStage.show();
        });

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
}