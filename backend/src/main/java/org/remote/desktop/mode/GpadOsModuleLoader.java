package org.remote.desktop.mode;

import lombok.RequiredArgsConstructor;
import org.desktop.remote.mode.GpadOsActionModule;
import org.remote.desktop.GamepadDesktopController;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.mode.modul.KeyboardModule;
import org.remote.desktop.mode.modul.XdoActionModule;
import org.remote.desktop.service.impl.StateService;
import org.remote.desktop.ui.CircleButtonsInputWidget;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.zapphyre.function.FunHelper.laterMerger;

@Configuration
@RequiredArgsConstructor
public class GpadOsModuleLoader {
    private static final String PLUGINS_DIR = "plugins";

    @Bean
    public Map<String, GpadOsActionModule> actuatorModules(StateService stateService,
                                                           CircleButtonsInputWidget widget) {
        ClassLoader pluginClassLoader = loadPlugins();
        XdoActionModule xdoActionModule = new XdoActionModule(stateService);
        KeyboardModule keyboardModule = new KeyboardModule(widget);
        // Manually load providers
        List<GpadOsActionModule> providers = new ArrayList<>();
        try {
            URL serviceFile = pluginClassLoader.getResource("META-INF/services/org.desktop.remote.module.winder.GpadOsActionModule");
            if (serviceFile != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(serviceFile.openStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty() && !line.startsWith("#")) {
                            try {
                                Class<?> clazz = pluginClassLoader.loadClass(line);
                                if (GpadOsActionModule.class.isAssignableFrom(clazz)) {
                                    GpadOsActionModule instance = (GpadOsActionModule) clazz.getDeclaredConstructor().newInstance();
                                    providers.add(instance);
                                }
                            } catch (Exception e) {
                                System.err.println("Failed to load/instantiate provider " + line + ": " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading service file: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Total providers loaded: " + providers.size());

        Map<String, GpadOsActionModule> moduleMap = providers.stream()
                .collect(Collectors.toMap(
                        GpadOsActionModule::getName,
                        Function.identity(),
                        laterMerger()
                ));
        moduleMap.put(xdoActionModule.getName(), xdoActionModule);
        moduleMap.put(keyboardModule.getName(), keyboardModule);

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