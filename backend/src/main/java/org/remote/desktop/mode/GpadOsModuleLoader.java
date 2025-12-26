package org.remote.desktop.mode;

import lombok.RequiredArgsConstructor;
import org.desktop.remote.mode.GpadOsActionModule;
import org.remote.desktop.GamepadDesktopController;
import org.remote.desktop.mode.modul.AnalogAdjustModule;
import org.remote.desktop.mode.modul.KeyboardModule;
import org.remote.desktop.mode.modul.YdoActionModule;
import org.remote.desktop.service.impl.StateService;
import org.remote.desktop.ui.CircleButtonsInputWidget;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.zapphyre.function.FunHelper.laterMerger;

@Configuration
@RequiredArgsConstructor
public class GpadOsModuleLoader {
    private static final String PLUGINS_DIR = "plugins";

    private final ApplicationEventPublisher eventPublisher;

    @Bean
    public Map<String, GpadOsActionModule> actuatorModules(StateService stateService,
                                                           CircleButtonsInputWidget widget) {
        ClassLoader pluginClassLoader = loadPlugins();
        List<GpadOsActionModule> providers = new ArrayList<>();

        ServiceLoader<GpadOsActionModule> loader =
                ServiceLoader.load(GpadOsActionModule.class, pluginClassLoader);

        for (GpadOsActionModule gpadOsActionModule : loader)
            providers.add(gpadOsActionModule);

//        XdoActionModule xdoActionModule = new XdoActionModule(stateService);
        YdoActionModule xdoActionModule = new YdoActionModule(stateService);
        KeyboardModule keyboardModule = new KeyboardModule(widget, stateService);
        AnalogAdjustModule analogAdjustModule = new AnalogAdjustModule(eventPublisher);

        // Manually load providers

        System.out.println("Total providers loaded: " + providers.size());

        Map<String, GpadOsActionModule> moduleMap = providers.stream()
                .collect(Collectors.toMap(
                        GpadOsActionModule::getName,
                        Function.identity(),
                        laterMerger()
                ));
        moduleMap.put(xdoActionModule.getName(), xdoActionModule);
        moduleMap.put(keyboardModule.getName(), keyboardModule);
        moduleMap.put(analogAdjustModule.getName(), analogAdjustModule);

        System.out.println("Module map size: " + moduleMap.size());
        moduleMap.forEach((name, module) -> System.out.println("Module: " + name + " -> " + module.getClass().getName()));

        return moduleMap;
    }

    private static ClassLoader loadPlugins() {
        try {
            File pluginsDir = new File(PLUGINS_DIR);
            if (!pluginsDir.exists() || !pluginsDir.isDirectory() || !pluginsDir.canRead()) {
                return GamepadDesktopController.class.getClassLoader();
            }

            File[] jarFiles = pluginsDir.listFiles((dir, name) -> name.endsWith(".jar"));
            List<URL> urls = new ArrayList<>();
            if (jarFiles != null) {
                for (File jar : jarFiles) {
                    try {
                        urls.add(jar.toURI().toURL());
                    } catch (MalformedURLException e) {
                        System.err.println("Invalid JAR URL: " + jar.getAbsolutePath());
                    }
                }
            }
            return new URLClassLoader(urls.toArray(new URL[0]), GamepadDesktopController.class.getClassLoader());
        } catch (Exception e) {
            System.err.println("Error in loadPlugins: " + e.getMessage());
            e.printStackTrace();
            return GamepadDesktopController.class.getClassLoader();
        }
    }
}