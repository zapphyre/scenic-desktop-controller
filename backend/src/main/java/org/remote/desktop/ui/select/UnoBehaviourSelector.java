package org.remote.desktop.ui.select;

import javafx.application.Platform;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.function.Function;

public class UnoBehaviourSelector<T> extends HBox {

    private final ColumnSelector<T> selector;

    public UnoBehaviourSelector() {
        setStyle("-fx-background-color: transparent;");
        setSpacing(20);
        setPadding(new javafx.geometry.Insets(20));

        selector = new ColumnSelector<>();
        getChildren().addAll(selector);
    }

    public void setColumns(List<? extends T> items,
                           Function<? super T, String> leftLabelExtractor) {
        Platform.runLater(() -> {
            selector.setItems(items, leftLabelExtractor);
        });
    }

    public void selectNext() {
        selector.selectNext();
    }

    public void selectPrevious() {
        selector.selectPrevious();
    }

    public T getSelected() {
        return selector.getSelected();
    }
}
