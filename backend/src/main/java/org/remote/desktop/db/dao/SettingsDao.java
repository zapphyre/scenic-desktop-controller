package org.remote.desktop.db.dao;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.Setting;
import org.remote.desktop.db.repository.SettingsRepository;
import org.remote.desktop.mapper.SettingMapper;
import org.remote.desktop.model.dto.SettingDto;
import org.remote.desktop.property.SettingsProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.remote.desktop.db.entity.Setting.INST_NAME;

@Service
@Transactional
@RequiredArgsConstructor
public class SettingsDao {

    public static final String WINDER_SCENE_NAME = "winder_scene";

    private final SettingsRepository settingsRepository;
    private final SettingsProperties settingsProperties;
    private final SettingMapper settingMapper;

    @Value("${server.port:8081}")
    private int port;

    @PostConstruct
    void deleteAll() {
        Setting current = settingsRepository.findBySettingsInstance(INST_NAME)
                .orElseGet(() ->
                        settingsRepository.save(settingMapper.map(settingsProperties)));
//
//        settingsRepository.deleteAll();
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

    public void setPersistentInputMode(boolean persistentInputMode) {
        settingsRepository.findBySettingsInstance(INST_NAME)
                .ifPresent(q -> q.setPersistentPreciseInput(persistentInputMode));
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

    public Function<Boolean, Set<String>> updateAutoconn(String name) {
        return q -> settingsRepository.findBySettingsInstance(INST_NAME)
                .map(s -> {
                    if (q)
                        s.getAutoconnect().add(name);
                    else
                        s.getAutoconnect().remove(name);

                    return s;
                })
                .map(settingsRepository::save)
                .map(Setting::getAutoconnect)
                .orElseThrow();
    }
}
