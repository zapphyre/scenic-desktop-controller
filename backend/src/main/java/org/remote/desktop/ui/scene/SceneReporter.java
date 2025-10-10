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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SceneReporter extends Application {

    private Label label;
    private Popup popup;
    private TrayIcon trayIcon;
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

        // ---- Setup tray ----
        try {
            setupSystemTray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ---- Demo usage ----
        Platform.runLater(() -> {
            render("Hello Overlay!");
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    hide();
                    Thread.sleep(1000);
                    render("Back again!");
                } catch (InterruptedException ignored) {}
            }).start();
        });
    }

    private void setupSystemTray() throws IOException, AWTException {
        if (!SystemTray.isSupported()) return;

        BufferedImage image;
        try {
            image = ImageIO.read(getClass().getResource("/icon.png"));
        } catch (Exception e) {
            image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.fillOval(0, 0, 16, 16);
            g.dispose();
        }

        PopupMenu menu = new PopupMenu();

        MenuItem showItem = new MenuItem("Show overlay");
        showItem.addActionListener(e -> Platform.runLater(this::showOverlay));

        MenuItem hideItem = new MenuItem("Hide overlay");
        hideItem.addActionListener(e -> Platform.runLater(this::hide));

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            SystemTray.getSystemTray().remove(trayIcon);
            Platform.exit();
        });

        menu.add(showItem);
        menu.add(hideItem);
        menu.addSeparator();
        menu.add(exitItem);

        trayIcon = new TrayIcon(image, "Overlay Control", menu);
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(e -> Platform.runLater(this::showOverlay));

        SystemTray.getSystemTray().add(trayIcon);
    }

    private void showOverlay() {
        render("Overlay active!");
    }

    public void render(String sceneName) {
        Platform.runLater(() -> {
            label.setText(sceneName);
            label.applyCss();
            label.layout();

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double x = screenBounds.getMinX() + (screenBounds.getWidth() - label.getWidth()) / 2;
            double y = screenBounds.getMinY() + screenBounds.getHeight() - label.getHeight() - 100;

            if (!popup.isShowing()) {
                popup.show(ownerStage, x, y); // uses invisible owner stage
            } else {
                popup.setX(x);
                popup.setY(y);
            }
        });
    }

    public void hide() {
        Platform.runLater(() -> {
            if (popup.isShowing()) popup.hide();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
