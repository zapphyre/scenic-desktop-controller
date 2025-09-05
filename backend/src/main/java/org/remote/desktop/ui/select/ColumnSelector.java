package org.remote.desktop.ui.select;

import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ColumnSelector<T> extends VBox {
    private List<T> items = new ArrayList<>();
    private final List<Label> labels = new ArrayList<>();
    private int selectedIndex = -1;
    private final Border activeBorder = new Border(new BorderStroke(
            Color.rgb(255, 0, 0, 0.8),
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

    public ColumnSelector() {
        setStyle("-fx-background-color: transparent;");
        setSpacing(20);
    }

    public void setItems(Collection<? extends T> items, Function<? super T, String> labelExtractor) {
        this.items = new ArrayList<>();
        if (items != null) {
            this.items.addAll(items);
        }
        this.labels.clear();
        getChildren().clear();
        this.selectedIndex = -1;

        for (int i = 0; i < this.items.size(); i++) {
            T item = this.items.get(i);
            String text = labelExtractor.apply(item);

            Label label = new Label(text);
            label.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: limegreen; -fx-background-color: transparent;");
            label.setEffect(new Glow(0.8));

            final int index = i;
            label.setOnMouseClicked(event -> {
                selectedIndex = index;
                updateSelection(true);
            });

            getChildren().add(label);
            labels.add(label);
        }

        if (!items.isEmpty()) {
            selectedIndex = 0;
            updateSelection(true);
        }
    }

    public void selectNext() {
        if (selectedIndex < items.size() - 1) {
            selectedIndex++;
            updateSelection(true);
        }
    }

    public void selectPrevious() {
        if (selectedIndex > 0) {
            selectedIndex--;
            updateSelection(true);
        }
    }

    public T getSelected() {
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            return items.get(selectedIndex);
        }
        return null;
    }

    public void setActive(boolean isActive) {
        updateSelection(isActive);
    }

    private void updateSelection(boolean isActive) {
        for (Label label : labels) {
            label.setBorder(null);
        }
        if (selectedIndex >= 0 && selectedIndex < labels.size()) {
            labels.get(selectedIndex).setBorder(isActive ? activeBorder : inactiveBorder);
        }
    }

    public void select(T elem) {
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).equals(elem)) continue;

            selectedIndex = i;
            updateSelection(true);
        }
    }
}