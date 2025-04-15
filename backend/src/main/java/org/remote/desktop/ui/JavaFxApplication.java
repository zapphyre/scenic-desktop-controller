package org.remote.desktop.ui;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class JavaFxApplication extends Application {

    VariableGroupingInputWidget widget = new VariableGroupingInputWidget(2, Color.BURLYWOOD,
            0.4, Color.ORANGE, Color.BLACK,
            6, "title");

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

    public static void main(String[] args) {
        launch(args);
    }

}