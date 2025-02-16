package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class SaveNotifiaction extends Notification {

    public static void success(String message) {
        Notification.show(message, 2100, Position.TOP_END).open();
    }

    public static void error() {
        Notification errorSaving = Notification.show("Error Saving", 2100, Position.TOP_END);
        errorSaving.addThemeVariants(NotificationVariant.LUMO_ERROR);
        errorSaving.open();
    }
}
