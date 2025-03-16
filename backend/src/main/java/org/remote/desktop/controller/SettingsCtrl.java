package org.remote.desktop.controller;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.dto.SettingDto;
import org.springframework.web.bind.annotation.*;

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
}
