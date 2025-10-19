package org.remote.desktop.db.dao;

import lombok.RequiredArgsConstructor;
import org.asmus.model.GamepadDevice;
import org.remote.desktop.db.entity.Gamepad;
import org.remote.desktop.db.entity.GamepadMode;
import org.remote.desktop.db.repository.GamepadModeRepository;
import org.remote.desktop.db.repository.GamepadRepository;
import org.remote.desktop.mapper.GamepadMapper;
import org.remote.desktop.model.dto.GamepadDto;
import org.remote.desktop.model.dto.GamepadModeDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@Transactional
@RequiredArgsConstructor
public class GamepadModeDao {

    private final GamepadModeRepository gamepadModeRepository;
    private final GamepadRepository gamepadRepository;
    private final GamepadMapper  gamepadMapper;

    public List<GamepadDto> getAllGamepads() {
        return gamepadRepository.findAll().stream()
                .map(gamepadMapper::map)
                .toList();
    }

    public GamepadModeDto getGamepadModeOrCreateDesktop(GamepadDevice device) {
        return Optional.ofNullable(device)
                .map(GamepadDevice::name)
                .map(this::getGamepadModeByDeviceName)
                .orElseGet(() -> gamepadMapper.map(createGamepadDeviceInDesktopMode(device)));
    }

    GamepadMode createGamepadDeviceInDesktopMode(GamepadDevice device) {
        return Optional.ofNullable(device)
                .map(gamepadMapper::map)
                .map(gamepadRepository::save)
                .map(q -> GamepadMode.builder()
                        .device(q)
                        .mode("DESKTOP")
                        .build()
                )
                .map(gamepadModeRepository::save)
                .orElseGet(() -> createGamepadDeviceInDesktopMode(device));
//                .orElseThrow(() -> new RuntimeException("Game pad device not found: "  + device));
    }

    public GamepadModeDto getGamepadModeByDeviceName(String deviceName) {
        return gamepadModeRepository.findByDeviceName(deviceName)
                .map(gamepadMapper::map)
                .orElse(null);
    }

    public Consumer<String> setModeFor(GamepadDevice device) {
        return mode -> gamepadModeRepository.findByDeviceName(device.name())
                .map(q -> q.withMode(mode))
                .map(gamepadModeRepository::save)
                .orElseGet(() -> createGamepadDeviceInDesktopMode(device));
    }
}
