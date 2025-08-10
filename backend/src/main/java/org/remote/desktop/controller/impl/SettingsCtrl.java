package org.remote.desktop.controller.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.dto.SettingDto;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("api/settings")
@RequiredArgsConstructor
public class SettingsCtrl {

    private final SettingsDao settingsDao;

    @GetMapping
    public SettingDto getSettings() {
        return settingsDao.getSettings();
    }

    @PutMapping
    public void update(@RequestBody SettingDto dto) {
        settingsDao.update(dto);
    }

    @PutMapping("autoconnect/{name}")
    public Set<String> updateAutoconnect(@RequestBody boolean autoconnect, @PathVariable("name") String name) {
        return settingsDao.updateAutoconn(name).apply(autoconnect);
    }
}
