package org.remote.desktop.ui.trigger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TriggerSelector<T> extends HBox {
    private final ColumnSelector<T> leftSelector;
    private final ColumnSelector<T> rightSelector;
    private int activeColumn = 0; // 0: left, 1: right

    public TriggerSelector() {
        setStyle("-fx-background-color: transparent;");
        setSpacing(20);
        setPadding(new javafx.geometry.Insets(20));

        leftSelector = new ColumnSelector<>();
        rightSelector = new ColumnSelector<>();
        getChildren().addAll(leftSelector, rightSelector);
    }

    public void setColumns(List<T> leftItems, List<T> rightItems, Function<T, String> labelExtractor) {
        leftSelector.setItems(leftItems, labelExtractor);
        rightSelector.setItems(rightItems, labelExtractor);
        updateActiveColumn();
    }

    public void selectNext() {
        if (activeColumn == 0) {
            leftSelector.selectNext();
        } else {
            rightSelector.selectNext();
        }
    }

    public void selectPrevious() {
        if (activeColumn == 0) {
            leftSelector.selectPrevious();
        } else {
            rightSelector.selectPrevious();
        }
    }

    private void switchToLeft() {
        if (activeColumn != 0) {
            activeColumn = 0;
            updateActiveColumn();
        }
    }

    private void switchToRight() {
        if (activeColumn != 1) {
            activeColumn = 1;
            updateActiveColumn();
        }
    }

    private void updateActiveColumn() {
        leftSelector.setActive(activeColumn == 0);
        rightSelector.setActive(activeColumn == 1);
    }

    public Map.Entry<T, T> getSelected() {
        return new AbstractMap.SimpleEntry<>(leftSelector.getSelected(), rightSelector.getSelected());
    }

    public static class TriggerSelectApplication extends Application {
        @Override
        public void start(Stage primaryStage) {
            TriggerSelector<String> selector = new TriggerSelector<>();

            List<String> leftSample = List.of("Left Trigger 1", "Left Trigger 2", "Left Trigger 3", "Left Trigger 4");
            List<String> rightSample = List.of("Right Trigger A", "Right Trigger B", "Right Trigger C");

            selector.setColumns(leftSample, rightSample, item -> item);

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

            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setTitle("Trigger Selector");
            primaryStage.setScene(scene);
            primaryStage.show();

            selector.requestFocus();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
}