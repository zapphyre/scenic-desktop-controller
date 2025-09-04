package org.remote.desktop.mode;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.GamepadDesktopController;
import org.remote.desktop.mode.modul.GpadOsActionModule;
import org.remote.desktop.mode.modul.impl.XdoActionModule;
import org.remote.desktop.service.impl.StateService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.zapphyre.function.FunHelper.funky;

@Configuration
@RequiredArgsConstructor
public class GpadOsModuleLoader {

    private static final String PLUGINS_DIR = "plugins";

    @Bean("actorMap")
    public Map<String, GpadOsActionModule> actuatorModules(StateService stateService) {
        ClassLoader pluginClassLoader = loadPlugins();

        ServiceLoader<GpadOsActionModule> loader = ServiceLoader.load(GpadOsActionModule.class, pluginClassLoader);
        XdoActionModule xdoActionModule = new XdoActionModule(stateService);

        return loader.stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.collectingAndThen(
                                Collectors.toMap(GpadOsActionModule::getName, Function.identity()),
                                funky(p -> p.put(xdoActionModule.getName(), xdoActionModule))
                            )
                );
    }

    private static ClassLoader loadPlugins() {
        return Optional.of(new File(PLUGINS_DIR))
                .filter(File::exists)
                .filter(File::canRead)
                .filter(File::isDirectory)
                .map(File::listFiles).stream()
                .flatMap(Arrays::stream)
                .filter(q -> q.getName().endsWith(".jar"))
                .map(File::toURI)
                .map(q -> {
                    try {
                        return q.toURL();
                    } catch (MalformedURLException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toList(), q -> new URLClassLoader(q.toArray(new URL[0]), GamepadDesktopController.class.getClassLoader())));
    }
}
