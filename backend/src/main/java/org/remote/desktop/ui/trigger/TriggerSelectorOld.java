package org.remote.desktop.ui.trigger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.remote.desktop.util.BidirectionalSpliterator;

import java.util.*;
import java.util.function.Function;

public class TriggerSelectorOld<T> extends GridPane {

    private List<T> leftItems = new ArrayList<>();
    private List<T> rightItems = new ArrayList<>();
    private Function<T, String> labelExtractor;
    private List<Label> leftLabels = new ArrayList<>();
    private List<Label> rightLabels = new ArrayList<>();
    private int leftSelected = -1;
    private int rightSelected = -1;
    private int currentColumn = 0; // 0: left, 1: right

    private final Border activeBorder = new Border(new BorderStroke(
            Color.rgb(255, 0, 0, 0.8), // Reddish with slight transparency
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            new BorderWidths(2)
    ));
    private final Border inactiveBorder = new Border(new BorderStroke(
            Color.WHITE,
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            new BorderWidths(2)
    ));

    public TriggerSelectorOld() {
        setStyle("-fx-background-color: transparent;");
        setHgap(20);
        setVgap(20);
        setPadding(new javafx.geometry.Insets(20));
    }

    public void setColumns(List<T> left, List<T> right, Function<T, String> labelExtractor) {
        this.leftItems = left != null ? new ArrayList<>(left) : new ArrayList<>();
        this.rightItems = right != null ? new ArrayList<>(right) : new ArrayList<>();
        this.labelExtractor = labelExtractor != null ? labelExtractor : Object::toString;
        this.leftSelected = -1;
        this.rightSelected = -1;
        this.currentColumn = 0;
        this.leftLabels.clear();
        this.rightLabels.clear();
        getChildren().clear();

        int maxRows = Math.max(this.leftItems.size(), this.rightItems.size());

        for (int row = 0; row < maxRows; row++) {
            if (row < this.leftItems.size()) {
                T item = this.leftItems.get(row);
                String text = this.labelExtractor.apply(item);

                Label label = new Label(text);
                label.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: limegreen; -fx-background-color: transparent;");
                label.setEffect(new Glow(0.8));

                final int r = row;
                label.setOnMouseClicked(event -> {
                    currentColumn = 0;
                    leftSelected = r;
                    updateSelection();
                });

                add(label, 0, row);
                leftLabels.add(label);
            }

            if (row < this.rightItems.size()) {
                T item = this.rightItems.get(row);
                String text = this.labelExtractor.apply(item);

                Label label = new Label(text);
                label.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: limegreen; -fx-background-color: transparent;");
                label.setEffect(new Glow(0.8));

                final int r = row;
                label.setOnMouseClicked(event -> {
                    currentColumn = 1;
                    rightSelected = r;
                    updateSelection();
                });

                add(label, 1, row);
                rightLabels.add(label);
            }
        }

        // Default selections
        if (!leftItems.isEmpty()) {
            leftSelected = 0;
        }
        if (!rightItems.isEmpty()) {
            rightSelected = 0;
        }
        updateSelection();
    }

    private void updateSelection() {
        BidirectionalSpliterator<T> bisplit = new BidirectionalSpliterator<>(leftItems);

        for (Label label : leftLabels) {
            label.setBorder(null);
        }
        for (Label label : rightLabels) {
            label.setBorder(null);
        }
        if (leftSelected >= 0 && leftSelected < leftLabels.size()) {
            leftLabels.get(leftSelected).setBorder(currentColumn == 0 ? activeBorder : inactiveBorder);
        }
        if (rightSelected >= 0 && rightSelected < rightLabels.size()) {
            rightLabels.get(rightSelected).setBorder(currentColumn == 1 ? activeBorder : inactiveBorder);
        }
    }

    public void selectNext() {
        if (currentColumn == 0 && leftSelected < leftItems.size() - 1) {
            leftSelected++;
        } else if (currentColumn == 1 && rightSelected < rightItems.size() - 1) {
            rightSelected++;
        }
        updateSelection();
    }

    public void selectPrevious() {
        if (currentColumn == 0 && leftSelected > 0) {
            leftSelected--;
        } else if (currentColumn == 1 && rightSelected > 0) {
            rightSelected--;
        }
        updateSelection();
    }

    public Map.Entry<T, T> getSelected() {
        T left = (leftSelected >= 0 && leftSelected < leftItems.size()) ? leftItems.get(leftSelected) : null;
        T right = (rightSelected >= 0 && rightSelected < rightItems.size()) ? rightItems.get(rightSelected) : null;
        return new AbstractMap.SimpleEntry<>(left, right);
    }

    public static class TriggerSelectApplication extends Application {
        @Override
        public void start(Stage primaryStage) {
            TriggerSelectorOld<String> selector = new TriggerSelectorOld<>();

            // Sample data
            List<String> leftSample = List.of(
                    "Left Trigger 1",
                    "Left Trigger 2",
                    "Left Trigger 3",
                    "Left Trigger 4"
            );
            List<String> rightSample = List.of(
                    "Right Trigger A",
                    "Right Trigger B",
                    "Right Trigger C"
            );

            selector.setColumns(leftSample, rightSample, item -> item);

            // Handle keyboard navigation
            selector.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case DOWN:
                        selector.selectNext();
                        break;
                    case UP:
                        selector.selectPrevious();
                        break;
                    case LEFT:
                        if (selector.currentColumn == 1) {
                            selector.currentColumn = 0;
                            if (selector.leftSelected == -1 && !selector.leftItems.isEmpty()) {
                                selector.leftSelected = 0;
                            }
                        }
                        selector.updateSelection();
                        break;
                    case RIGHT:
                        if (selector.currentColumn == 0) {
                            selector.currentColumn = 1;
                            if (selector.rightSelected == -1 && !selector.rightItems.isEmpty()) {
                                selector.rightSelected = 0;
                            }
                        }
                        selector.updateSelection();
                        break;
                }
            });

            Scene scene = new Scene(selector, 400, 300);
            scene.setFill(Color.TRANSPARENT);

            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setTitle("Trigger Selector");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Ensure the selector can receive key events
            selector.requestFocus();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
}