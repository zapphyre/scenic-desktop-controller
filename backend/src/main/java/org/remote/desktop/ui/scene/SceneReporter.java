package org.remote.desktop.ui.scene;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SceneReporter extends Application {

    private Label label;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.label = new Label();

        // Styling the label
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: limegreen; -fx-background-color: transparent;");
        label.setEffect(new Glow(0.8));

        AnchorPane root = new AnchorPane();
        root.setBackground(null);
        root.getChildren().add(label);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Trigger Selector");
        primaryStage.setScene(scene);
        primaryStage.show();

        Platform.runLater(() -> {
            label.applyCss();
            label.layout();
            primaryStage.sizeToScene();
            positionStageBottomCenter();
        });

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.hide();
        });

    }

    public void close() {
        Platform.runLater(() -> primaryStage.hide());
    }

    public void render(String sceneName) {
        label.setText(sceneName);
        Platform.runLater(() -> {
            label.applyCss();
            label.layout();
            primaryStage.sizeToScene();
            positionStageBottomCenter();
            primaryStage.setAlwaysOnTop(true);
            primaryStage.show();
        });
    }

    /**
     * Positions the stage 100px above the bottom of the primary screen, centered horizontally.
     */
    private void positionStageBottomCenter() {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        double stageWidth = primaryStage.getWidth();
        double stageHeight = primaryStage.getHeight();

        double x = screenBounds.getMinX() + (screenBounds.getWidth() - stageWidth) / 2;
        double y = screenBounds.getMinY() + screenBounds.getHeight() - stageHeight - 100;

        primaryStage.setX(x);
        primaryStage.setY(Math.max(y, 0)); // Prevent it from going offscreen if screen is small
    }

    public static void main(String[] args) {
        launch(args);
    }
}
