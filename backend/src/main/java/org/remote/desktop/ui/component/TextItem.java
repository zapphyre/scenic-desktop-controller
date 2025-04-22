package org.remote.desktop.ui.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TextItem extends StackPane {
    private final TextField textNode;

    public TextItem(String text) {
        textNode = new TextField(text);
        textNode.setFont(Font.font(32));
        textNode.setBackground(Background.EMPTY);
        textNode.setFocusTraversable(false);
        textNode.setAlignment(Pos.CENTER_LEFT); // Align text to the left

        setPadding(Insets.EMPTY);
        getChildren().add(textNode);
        setBorderVisible(false);
    }

    private void updatePreferredWidth() {
        Text text = new Text(textNode.getText());
        text.setFont(textNode.getFont());
        double textWidth = text.getLayoutBounds().getWidth();
        // Add small padding to avoid clipping
        textNode.setPrefWidth(textWidth + 5); // Adjust padding as needed
    }

    public void deselect() {
        textNode.deselect();
    }

    public void setText(String text) {
        textNode.setText(text);
    }

    public String getText() {
        return textNode.getText();
    }

    public void setFont(Font font) {
        textNode.setFont(font);
    }

    public TextItem getTextField() {
        return this;
    }

    public void moveCursorLeft() {
        textNode.requestFocus();
        textNode.backward();
    }

    public void moveCursorRight() {
        textNode.requestFocus();
        textNode.forward();
    }

    public int getCursorPosition() {
        return textNode.getCaretPosition();
    }

    public void setBorderVisible(boolean visible) {
        if (visible)
            this.setBorder(new Border(new BorderStroke(
                    Color.BURLYWOOD,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(5),
                    new BorderWidths(4)
            )));
        else
            this.setBorder(Border.EMPTY);
    }
}
