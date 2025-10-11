package org.remote.desktop.ui.tray;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Popup;
import javafx.stage.Screen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class LinuxTray {

    private Popup popup;
    private TrayIcon trayIcon;

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

        popup = new Popup();
        popup.setAutoFix(false);
        popup.setAutoHide(false);
        popup.getContent().add(new javafx.scene.control.Label(""));
        popup.hide();

        setupSystemTray();

        SystemTray.getSystemTray().add(trayIcon);
    }

    public void showOverlay() {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
//        double x = screenBounds.getMinX() + (screenBounds.getWidth() - label.getWidth()) / 2;
//        double y = screenBounds.getMinY() + screenBounds.getHeight() - label.getHeight() - 100;
//
//        if (!popup.isShowing()) {
//            popup.show(ownerStage, x, y); // uses invisible owner stage
//        } else {
//            popup.setX(x);
//            popup.setY(y);
//        }
    }

    public void hide() {
        Platform.runLater(popup::hide);
    }
}
