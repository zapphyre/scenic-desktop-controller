package org.remote.desktop.ui.component;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TextItem extends StackPane {
    private final Text textNode;

    public TextItem(String text) {
        textNode = new Text(text);
        textNode.setFont(Font.font(32));
//            TextFlow flow = new TextFlow(textNode);
//            this.getChildren().add(flow);
        this.getChildren().add(textNode);

        setBorderVisible(false);
    }

    public void setText(String text) {
        textNode.setText(text);
    }

    public String getText() {
        return textNode.getText();
    }

    public void setTextColor(Color color) {
        textNode.setFill(color);
    }

    public void setFont(javafx.scene.text.Font font) {
        textNode.setFont(font);
    }

    public void setBorderVisible(boolean visible) {
        if (visible) {
            this.setBorder(new Border(new BorderStroke(
                    Color.BURLYWOOD,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(5),
                    new BorderWidths(4)
            )));
        } else {
            this.setBorder(Border.EMPTY);
        }
    }
}
