package org.remote.desktop.ui;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.SneakyThrows;

public class JavaFxApplication extends Application {

    InputWidgetBase widget = new CircleButtonsInputWidget(90, 2, Color.BURLYWOOD,
            0.4, Color.ORANGE, Color.BLACK,
            6, "title", q -> {}, false, q -> {});

    @Override
    public void init() throws Exception {
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        widget.start(primaryStage);

        widget.render();
     }

    @Override
    public void stop() throws Exception {
    }

    @SneakyThrows
    public static void main(String[] args) {
        launch(args);
    }

}