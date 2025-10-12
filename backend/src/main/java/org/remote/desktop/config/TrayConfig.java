package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.service.impl.ModeService;
import org.remote.desktop.service.impl.StateService;
import org.remote.desktop.ui.select.mode.ModeSelector;
import org.remote.desktop.ui.tray.LinuxTray;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;

@Configuration
@RequiredArgsConstructor
public class TrayConfig {

    private final ModeSelector modeSelector;
    private final StateService stateService;
    private final ModeService modeService;

    @Bean
    public LinuxTray createLinuxTray() throws AWTException {
        LinuxTray linuxTray = new LinuxTray();

        linuxTray.setupSystemTray(modeSelector, modeService.getAllGamepads(), stateService::nullifyForced);

        return linuxTray;
    }
}
