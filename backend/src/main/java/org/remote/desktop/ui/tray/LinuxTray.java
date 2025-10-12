package org.remote.desktop.ui.tray;

import javafx.application.Platform;
import javafx.stage.Popup;
import org.asmus.model.GamepadDevice;
import org.remote.desktop.model.dto.GamepadDto;
import org.remote.desktop.ui.select.mode.ModeSelector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class LinuxTray {

    private Popup popup;
    private TrayIcon trayIcon;

    public void setupSystemTray(ModeSelector modeSelector, List<GamepadDto> gamepads, Runnable keysUp) throws AWTException {
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

        Menu adjust = new Menu("Adjust");

        gamepads.forEach(q -> adjust.add(new MenuItem(q.getName()) {{
            addActionListener(p -> modeSelector.getApplication().render(null, "", new GamepadDevice(q.getName(), q.getDev())));
        }}));

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            SystemTray.getSystemTray().remove(trayIcon);
            Platform.exit();
        });

        MenuItem keysUpItem = new MenuItem("Keys Up");
        keysUpItem.addActionListener(e -> keysUp.run());

        menu.add(adjust);
        menu.addSeparator();
        menu.add(keysUpItem);
        menu.addSeparator();
        menu.add(exitItem);

        trayIcon = new TrayIcon(image, "Overlay Control", menu);
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(e -> keysUp.run());

        popup = new Popup();
        popup.setAutoFix(false);
        popup.setAutoHide(false);

        SystemTray.getSystemTray().add(trayIcon);
    }

    public void hide() {
        Platform.runLater(popup::hide);
    }
}
