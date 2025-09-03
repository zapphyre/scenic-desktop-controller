package org.remote.desktop.mode;

import org.remote.desktop.GamepadDesktopController;
import org.remote.desktop.model.modul.GpadOsActionModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class GpadOsModuleLoader {

    private static final String PLUGINS_DIR = "plugins";

    @Bean
    public Map<String, GpadOsActionModule> getModules() {
        ClassLoader pluginClassLoader = loadPlugins();

        ServiceLoader<GpadOsActionModule> loader = ServiceLoader.load(GpadOsActionModule.class, pluginClassLoader);

        return loader.stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toMap(GpadOsActionModule::getName, Function.identity()));
    }

    private static ClassLoader loadPlugins() {
        try {
            File pluginsDir = new File(PLUGINS_DIR);
            if (!pluginsDir.exists() || !pluginsDir.isDirectory()) {
                return GamepadDesktopController.class.getClassLoader();  // Fallback to default classloader
            }

            List<URL> urls = new ArrayList<>();
            File[] files = pluginsDir.listFiles((dir, name) -> name.endsWith(".jar"));
            if (files != null) {
                for (File file : files) {
                    urls.add(file.toURI().toURL());
                }
            }

            // Create a child classloader with plugins
            return new URLClassLoader(urls.toArray(new URL[0]), GamepadDesktopController.class.getClassLoader());
        } catch (Exception e) {
            // Handle error (e.g., log and fallback)
            return GamepadDesktopController.class.getClassLoader();
        }
    }
}
