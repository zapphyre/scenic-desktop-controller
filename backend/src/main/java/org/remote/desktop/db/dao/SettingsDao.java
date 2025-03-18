package org.remote.desktop.db.dao;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.Setting;
import org.remote.desktop.db.repository.SettingsRepository;
import org.remote.desktop.mapper.SettingMapper;
import org.remote.desktop.model.dto.SettingDto;
import org.remote.desktop.property.SettingsProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SettingsDao {

    private final SettingsRepository settingsRepository;
    private final SettingsProperties settingsProperties;
    private final SettingMapper settingMapper;

    public void update(SettingDto dto) {
        settingsRepository.deleteAll();
        settingsRepository.flush();

        Optional.ofNullable(dto)
                .map(settingMapper::map)
                .ifPresent(settingsRepository::save);
    }

    public boolean disconnectOnRemoteConnect() {
        return getSettings().isDisconnectLocalOnRemoteConnection();
    }

    public String getInstanceName() {
        return getSettings().getInstanceName();
    }

    public SettingDto getSettings() {
        return Optional.of(settingsRepository.findAll())
                .map(q -> q.isEmpty() ? null : q.getFirst())
                .map(settingMapper::map)
                .orElseGet(() -> settingMapper.mapProps(settingsProperties));
    }

    public Integer getPort() {
        return getSettings().getPort();
    }

    public String getIpAddress() {
        return getSettings().getIpAddress();
    }
}
