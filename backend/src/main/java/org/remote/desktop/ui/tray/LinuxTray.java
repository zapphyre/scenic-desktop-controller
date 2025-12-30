package org.remote.desktop.ui.tray;

import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;
import javafx.application.Platform;
import org.asmus.model.GamepadDevice;
import org.remote.desktop.model.dto.GamepadDto;
import org.remote.desktop.ui.select.mode.ModeSelector;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;

public class LinuxTray {

    private SystemTray systemTray;

    public void setupSystemTray(ModeSelector modeSelector, List<GamepadDto> gamepads, Runnable keysUp) {
        systemTray = SystemTray.get();
        SystemTray.FORCE_TRAY_TYPE = SystemTray.TrayType.AppIndicator;  // Key line!

        // Optional: Enable debug logs to confirm backend
        SystemTray.DEBUG = true;
        if (systemTray == null) {
            System.err.println("SystemTray is not supported on this platform.");
            return;
        }

        // Load icon (fallback to simple generated image if missing)
        BufferedImage image;
        try {
            image = ImageIO.read(getClass().getResource("/icon.png"));
        } catch (Exception e) {
            image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB); // Larger for better scaling
            var g = image.createGraphics();
            g.setColor(java.awt.Color.BLUE);
            g.fillOval(0, 0, 64, 64);
            g.dispose();
        }

        systemTray.setImage(image);
        systemTray.setStatus("Overlay Control"); // Tooltip (may not show on all Linux backends)

        // Left-click on tray icon triggers Keys Up
//        systemTray.setCallback(e -> Platform.runLater(keysUp::run));

        Menu mainMenu = systemTray.getMenu();

        // Submenu "Adjust"
        Menu adjust = new Menu("Adjust");
        mainMenu.add(adjust);

        for (GamepadDto q : gamepads)
            adjust.add(new MenuItem(q.getName(), e ->
                    modeSelector.getApplication().render(null, "", new GamepadDevice(q.getName(), q.getDev()))
            ));

        // Keys Up item
        mainMenu.add(new MenuItem("Keys Up", e -> Platform.runLater(keysUp::run)));

//        mainMenu.addSeparator();

        // Exit item
        mainMenu.add(new MenuItem("Exit", e -> {
            systemTray.shutdown(); // Cleans up native resources
            Platform.exit();
        }));
    }

    // No hide() needed — the tray icon is always visible when added.
    // If you want to temporarily remove it, call systemTray.remove() and re-add later.
}