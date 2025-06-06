package org.remote.desktop.ui.component;

import javafx.application.Platform;
import javafx.geometry.Pos;
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

    public TextContainer(boolean margins) {
        setBackground(Background.EMPTY);
        setSpacing(4);
        setAlignment(Pos.CENTER);
    }

    public TextContainer() {
        this(false);
    }

    public void addText(String text) {
        TextItem item = new TextItem(text);
        items.add(item);

        if (!this.getChildren().isEmpty())
            this.getChildren().add(new TextItem("  "));

        this.getChildren().add(item);
//        items.forEach(TextItem::deselect);
    }

    public void replaceContent(List<String> content) {
        items.clear();
        Platform.runLater(() -> {
            this.getChildren().clear();
            content.forEach(this::addText);
        });
    }

    public void clear() {
//        System.out.println("clearing all items");
        items.clear();
        this.getChildren().clear();
    }

    public String getTextContent() {
        return this.items.stream().map(TextItem::getText).collect(Collectors.joining(" "));
    }

    public String getWord(int index) {
        if (index >= 0 && index < items.size())
            return items.get(index).getText();
        return "";
    }

    public TextItem getLastItem() {
        return items.getLast();
    }

    public int getWordsCount() {
        return items.size();
    }

    public void setTextBorderVisible(int index) {
        System.out.println("setTextBorderVisible: " + index);
        if (index >= 0 && index < items.size())
            for (int i = 0; i < items.size(); i++)
                items.get(i).setBorderVisible(i == index);
    }

    public void setContainerBackground(Color color) {
        this.setBackground(new Background(new BackgroundFill(
                color,
                CornerRadii.EMPTY,
                javafx.geometry.Insets.EMPTY
        )));
    }
}
