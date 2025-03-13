package org.remote.desktop.controller;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.SettingsDao;
import org.remote.desktop.model.dto.SettingDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("settings")
@RequiredArgsConstructor
public class SettingsCtrl {

    private final SettingsDao settingsDao;

    @GetMapping
    public SettingDto getSettings() {
        return settingsDao.getSettings();
    }

    @PutMapping
    public void update(SettingDto dto) {
        settingsDao.update(dto);
    }
}
