package org.remote.desktop.ui.component;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TextContainer extends HBox {
    private final List<TextItem> items = new ArrayList<>();

    public TextContainer() {
        setSpacing(10);
        setPadding(new javafx.geometry.Insets(10));
    }

    public void addText(String text) {
        TextItem item = new TextItem(text);
        items.add(item);
        this.getChildren().add(item);
    }

    public void clear() {
        items.clear();
        this.getChildren().clear();
    }

    public String getTextContent() {
        return this.items.stream().map(TextItem::getText).collect(Collectors.joining(""));
    }

    public void setTextBorderVisible(int index, boolean visible) {
        if (index >= 0 && index < items.size()) {
            items.get(index).setBorderVisible(visible);
        }
    }

    public void setContainerBackground(Color color) {
        this.setBackground(new Background(new BackgroundFill(
                color,
                CornerRadii.EMPTY,
                javafx.geometry.Insets.EMPTY
        )));
    }
}
