package org.remote.desktop.ui.trigger;

import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TriggerSelector extends HBox {
    private final ColumnSelector<? extends UiSelectable<?>> leftSelector;
    private final ColumnSelector<? extends UiSelectable<?>> rightSelector;
    private int activeColumn = 0; // 0: left, 1: right

    public TriggerSelector() {
        setStyle("-fx-background-color: transparent;");
        setSpacing(20);
        setPadding(new javafx.geometry.Insets(20));

        leftSelector = new ColumnSelector<>();
        rightSelector = new ColumnSelector<>();
        getChildren().addAll(leftSelector, rightSelector);
    }

    public <L extends UiSelectable<?>, R extends UiSelectable<?>> SelectedGetter<L, R> setColumns(List<L> leftItems, List<R> rightItems, Function<L, String> leftLabelExtractor, Function<R, String> rightLabelExtractor) {
        leftSelector.<L>setItems(leftItems, leftLabelExtractor);
        rightSelector.<R>setItems(rightItems, rightLabelExtractor);
        updateActiveColumn();

        return () -> Map.entry(leftItems.get(activeColumn), rightItems.get(activeColumn));
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

//    public Map.Entry<L, R> getSelected() {
//        return Map.entry(leftSelector.getSelected(), rightSelector.getSelected());
//    }
}