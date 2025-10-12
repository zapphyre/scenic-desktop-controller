package org.remote.desktop.ui.select;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DuoBehaviourSelector<L, R> extends HBox {

    private final ColumnSelector<L> leftSelector;
    private final ColumnSelector<R> rightSelector;
    private int activeColumn = 0; // 0: left, 1: right

    // Invisible utility stage (used later by external popup)
    private Stage ownerStage;

    public DuoBehaviourSelector() {
        setSpacing(20);
        setPadding(new Insets(20));
        setBackground(Background.EMPTY);
        setStyle("-fx-background-color: rgba(0,0,0,0.65); -fx-background-radius: 12;");

        leftSelector = new ColumnSelector<>();
        rightSelector = new ColumnSelector<>();

        getChildren().addAll(leftSelector, rightSelector);
    }

    private void initOwnerStage() {
        if (ownerStage != null) return; // already initialized
        ownerStage = new Stage(StageStyle.UTILITY);
        ownerStage.setOpacity(0);
        ownerStage.setWidth(1);
        ownerStage.setHeight(1);
        ownerStage.setX(-10000);
        ownerStage.setY(-10000);
        ownerStage.setScene(new Scene(new HBox(), Color.TRANSPARENT));
        ownerStage.show();
    }

    public Stage getOwnerStage() {
        return ownerStage;
    }

    // --- Behavior selectors ---

    public void setColumns(List<? extends L> leftItems, List<? extends R> rightItems,
                           Function<? super L, String> leftLabelExtractor,
                           Function<? super R, String> rightLabelExtractor) {
        // Prepare invisible owner stage after FX runtime is ready
        Platform.runLater(this::initOwnerStage);

        Platform.runLater(() -> {
            leftSelector.setItems(leftItems, leftLabelExtractor);
            rightSelector.setItems(rightItems, rightLabelExtractor);
            updateActiveColumn();
        });
    }

    public void selectNext() {
        if (activeColumn == 0) leftSelector.selectNext();
        else rightSelector.selectNext();
    }

    public void selectPrevious() {
        if (activeColumn == 0) leftSelector.selectPrevious();
        else rightSelector.selectPrevious();
    }

    public void switchToLeft() {
        if (activeColumn != 0) {
            activeColumn = 0;
            updateActiveColumn();
        }
    }

    public void switchToRight() {
        if (activeColumn != 1) {
            activeColumn = 1;
            updateActiveColumn();
        }
    }

    private void updateActiveColumn() {
        leftSelector.setActive(activeColumn == 0);
        rightSelector.setActive(activeColumn == 1);
    }

    public Map.Entry<L, R> getSelected() {
        return Map.entry(leftSelector.getSelected(), rightSelector.getSelected());
    }

    public void select(L l, R r) {
        leftSelector.select(l);
        rightSelector.select(r);
    }
}
