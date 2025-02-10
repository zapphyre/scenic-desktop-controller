package org.remote.desktop.ui;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Push
@Theme(themeClass = Lumo.class, variant = Lumo.DARK)
@PWA(name = "Gamepad Configurer UI", shortName = "GPadCtrl UI")
public class ShellView implements AppShellConfigurator {
}
