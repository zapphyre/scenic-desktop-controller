package org.remote.desktop.ui.scene;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SceneReporter extends Application {

    private Label label;
    private Popup popup;
    private Stage ownerStage; // invisible owner

    @Override
    public void start(Stage stage) {
        // ---- Invisible owner stage for the popup ----
        ownerStage = new Stage(StageStyle.UTILITY);
        ownerStage.setOpacity(0);
        ownerStage.setWidth(1);
        ownerStage.setHeight(1);
        ownerStage.setX(-10000); // offscreen
        ownerStage.setY(-10000);
        ownerStage.setScene(new Scene(new StackPane(), Color.TRANSPARENT));
        ownerStage.show();

        // ---- Overlay label ----
        label = new Label("not-set-so-far");
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: limegreen;");
        label.setEffect(new Glow(0.8));

        popup = new Popup();
        popup.setAutoFix(false);
        popup.setAutoHide(false);
        popup.getContent().add(label);
        label.setMouseTransparent(true);
        popup.hide();
    }

    public void render(String sceneName) {
        Platform.runLater(() -> {
            label.setText(sceneName);
            label.applyCss();
            label.layout();

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double x = screenBounds.getMinX() + (screenBounds.getWidth() - label.getWidth()) / 2;
            double y = screenBounds.getMinY() + screenBounds.getHeight() - label.getHeight() - 100;

            popup.show(ownerStage, x, y); // uses invisible owner stage
        });
    }

    public void hide() {
        Platform.runLater(popup::hide);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
