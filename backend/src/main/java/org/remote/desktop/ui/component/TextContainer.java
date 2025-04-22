package org.remote.desktop.ui.component;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.remote.desktop.util.IdxWordTx;
import org.remote.desktop.util.WordGenFun;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.aspectj.bridge.Version.getText;

public class TextContainer extends HBox {
    private final List<TextItem> items = new ArrayList<>();

    public TextContainer(boolean margins) {
        setBackground(Background.EMPTY);
        setBorder(Border.stroke(Paint.valueOf(Color.BLACK.toString())));
        setSpacing(4);
    }

    public TextContainer() {
        this(false);
    }

    public void addText(String text) {
        TextItem item = new TextItem(text);
        items.add(item);
        this.getChildren().add(item);
        items.forEach(TextItem::deselect);
    }

    public void transformLast(WordGenFun txFun) {
        String toModify = "";
        String postfix = "";
        if (!items.isEmpty()) {
            TextItem last = items.getLast();
            String text = last.getText();
            toModify = text.substring(0, last.getCursorPosition());
            postfix = text.substring(last.getCursorPosition());
            System.out.println("toModify: " + toModify);
            System.out.println("postfix: " + postfix);

            this.getChildren().remove(last);
        }

        String transformed = txFun.transform(toModify) + postfix;
        addText(transformed);
    }

    public void transformLast(IdxWordTx idxWordTx) {
        if (items.isEmpty()) return;

        TextItem last = items.getLast();
        String transformed = idxWordTx
                .transforIdxWord(last.getCursorPosition())
                .transform(last.getText());

        this.getChildren().remove(last);
        addText(transformed);
    }

    public void clear() {
        items.clear();
        System.out.println("clearing all items");
        Platform.runLater(() -> this.getChildren().clear());
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
        return items.getLast().getTextField();
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
