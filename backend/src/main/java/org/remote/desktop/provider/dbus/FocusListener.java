package org.remote.desktop.provider.dbus;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.exceptions.DBusException;
import org.remote.desktop.provider.SceneNameContainer;
import org.remote.desktop.provider.SceneProvider;
import org.remote.desktop.service.impl.SceneManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class FocusListener implements SceneProvider {
    private static FocusReporterDBus proxy;

    private String windowName;

    public FocusListener(SceneNameContainer sceneNameContainer) throws DBusException {
        DBusConnection conn = DBusConnectionBuilder.forSessionBus().build();

        // Get remote proxy object
        proxy = conn.getRemoteObject(
                "org.zapphyre.Gpad.Controller",
                "/org/zapphyre/Gpad/Controller/FocusReporter",
                FocusReporterDBus.class
        );

        FocusReporterDBus.FocusedWindowInfo i = queryCurrentFocused(conn);
        windowName = i.title + "+" + i.wmClass;
        sceneNameContainer.setRecognizedSceneName(windowName);

        // Register signal handler — modern style
        conn.addSigHandler(
                FocusReporterDBus.FocusChanged.class,
                proxy,  // source object filter (optional but recommended)
                signal -> {
//                    System.out.printf("→ Focus → \"%s\" | class=%s | instance=%s | pid=%d%n",
//                            signal.title,
//                            signal.wmClass,
//                            signal.wmClassInstance,
//                            signal.pid);
                    windowName = signal.title + "+" + signal.wmClass;
                    System.out.println(windowName);
                    sceneNameContainer.setRecognizedSceneName(windowName);
                }
        );

        System.out.println("Listening for window focus changes... (Ctrl+C to stop)");
    }

    public FocusReporterDBus.FocusedWindowInfo queryCurrentFocused(DBusConnection conn) throws DBusException {
        FocusReporterDBus proxy = conn.getRemoteObject(
                "org.zapphyre.Gpad.Controller",
                "/org/zapphyre/Gpad/Controller/FocusReporter",
                FocusReporterDBus.class,
                false  // ← disables auto-start/activation attempt
        );

        return proxy.GetCurrent();
    }

    @Override
    public @NonNull String tryGetCurrentName() {
        return windowName;
    }
}