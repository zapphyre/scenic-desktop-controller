package org.remote.desktop.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@UIScope
@PageTitle("Gamepad Configurer UI")
@Route(value = "gamepados")
@SpringComponent
public class GamepadOsApp extends AppLayout {

    private final Tabs tabs = new Tabs();
    private final List<TabView> tabViews = new LinkedList<>();
    private final Map<Class<? extends Component>, Tab> routeToTab = new HashMap<>();

    public GamepadOsApp(GamepadActionConfig gamepadActionConfig, KeyboardStateConfig keyboardStateConfig) {
        HorizontalLayout navbarLayout = new HorizontalLayout();
        navbarLayout.setWidthFull();
        navbarLayout.setSpacing(true);
        navbarLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        tabs.setWidthFull();

        addTabView("Gamepad Configurer UI", gamepadActionConfig);
        addTabView("Sources Config", keyboardStateConfig);
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);

        // Handle tab selection
        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = tabs.getSelectedTab();
            TabView selected = tabViews.stream()
                    .filter(tabView -> tabView.tab.equals(selectedTab))
                    .findFirst()
                    .orElse(tabViews.get(0)); // Default to first tab
            setContent(selected.view);
        });

        navbarLayout.add(tabs);
        addToNavbar(navbarLayout);
    }

    private void addTabView(String title, Component component) {
        Tab tab = new Tab(title);
        TabView tabView = new TabView(tab, component);
        tabViews.add(tabView);
        tabs.add(tab);
    }

    private static class TabView {
        private final Tab tab;
        private final Component view;

        TabView(Tab tab, Component view) {
            this.tab = tab;
            this.view = view;
        }
    }
}
