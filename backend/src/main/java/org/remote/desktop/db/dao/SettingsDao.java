package org.remote.desktop.db.dao;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.repository.SettingsRepository;
import org.remote.desktop.mapper.SettingMapper;
import org.remote.desktop.model.dto.SettingDto;
import org.remote.desktop.property.SettingsProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.remote.desktop.db.entity.Setting.INST_NAME;

@Service
@Transactional
@RequiredArgsConstructor
public class SettingsDao {

    private final SettingsRepository settingsRepository;
    private final SettingsProperties settingsProperties;
    private final SettingMapper settingMapper;

    @Value("${server.port:8081}")
    private int port;

    @PostConstruct
    void deleteAll() {
        settingsRepository.deleteAll();
    }

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
        return settingsRepository.findBySettingsInstance(INST_NAME)
                .map(settingMapper::map)
                .orElseGet(() -> settingMapper.mapProps(settingsProperties));
    }

    public Integer getPort() {
        return Optional.ofNullable(getSettings().getPort())
                .orElse(port);
    }

    public String getIpAddress() {
        return getSettings().getIpAddress();
    }
}
