package org.remote.desktop.ui.select;

import javafx.application.Platform;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DuoBehaviourSelector<L, R> extends HBox {
    private final ColumnSelector<L> leftSelector;
    private final ColumnSelector<R> rightSelector;
    private int activeColumn = 0; // 0: left, 1: right

    public DuoBehaviourSelector() {
        setStyle("-fx-background-color: transparent;");
        setSpacing(20);
        setPadding(new javafx.geometry.Insets(20));

        leftSelector = new ColumnSelector<>();
        rightSelector = new ColumnSelector<>();
        getChildren().addAll(leftSelector, rightSelector);
    }

    public SelectedGetter<L, R> setColumns(List<? extends L> leftItems, List<? extends R> rightItems,
                                           Function<? super L, String> leftLabelExtractor,
                                           Function<? super R, String> rightLabelExtractor) {
        Platform.runLater(() -> {
            leftSelector.setItems(leftItems, leftLabelExtractor);
            rightSelector.setItems(rightItems, rightLabelExtractor);
            updateActiveColumn();
        });

        return this::getSelected;
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