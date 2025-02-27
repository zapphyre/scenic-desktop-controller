package org.remote.desktop.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.HashMap;
import java.util.Map;

@UIScope
@PageTitle("Gamepad Configurer UI")
@Route(value = "gamepados")
@SpringComponent
public class GamepadOsApp extends AppLayout {

    private final Tabs tabs = new Tabs();
    private final Map<Tab, TabView> tabViewMap = new HashMap<>();

    public GamepadOsApp(GamepadActionConfig gamepadActionConfig, KeyboardStateConfig keyboardStateConfig) {
        HorizontalLayout navbarLayout = new HorizontalLayout();
        navbarLayout.setWidthFull();
        navbarLayout.setSpacing(true);
        navbarLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        tabs.setWidthFull();

        tabs.add(createTab("Gamepad Configurer UI", gamepadActionConfig));
        tabs.add(createTab("Sources Config", keyboardStateConfig));
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);

        // Handle tab selection
        tabs.addSelectedChangeListener(event -> setContent(tabViewMap.get(tabs.getSelectedTab()).view));

        HorizontalLayout padding = new HorizontalLayout();
        padding.setWidthFull();
        padding.setSpacing(true);
        padding.setPadding(true);
        padding.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        padding.add(VaadinIcon.GAMEPAD.create());
        padding.add(VaadinIcon.CTRL_A.create());
        padding.add(VaadinIcon.BROWSER.create());

        navbarLayout.add(tabs, padding);
        addToNavbar(navbarLayout);
    }

    private Tab createTab(String title, Component component) {
        Tab tab = new Tab(title);
        TabView tabView = new TabView(tab, component);
        tabViewMap.put(tab, tabView);

        return tab;
    }

    private record TabView(Tab tab, Component view) {
    }
}
