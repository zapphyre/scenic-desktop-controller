package org.remote.desktop.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.asmus.model.GamepadDevice;
import org.desktop.remote.mode.GpadOsActionModule;
import org.remote.desktop.db.dao.GamepadModeDao;
import org.remote.desktop.db.dao.ModeDao;
import org.remote.desktop.model.dto.GamepadDto;
import org.remote.desktop.model.vto.ModeVto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.zapphyre.function.FunHelper.funky;

@Service
@RequiredArgsConstructor
public class ModeService {

    private final ModeDao modeDao;
    private final StateService stateService;
    private final Map<String, GpadOsActionModule> moduleMap;
    private final GamepadModeDao gamepadModeDao;

    public boolean isCurrentGamepadModeScenic(GamepadDevice device) {
        return Optional.ofNullable(device)
                .map(this::getCurrentModeNameFor)
                .map(moduleMap::get)
                .orElseGet(this::getDesktopModule)
                .isScenic();
    }

    public String getCurrentModeNameFor(GamepadDevice device) {
        return Optional.ofNullable(device)
                .map(GamepadDevice::name)
                .map(gamepadModeDao::getGamepadModeByDeviceName)
                .orElseGet(() -> gamepadModeDao.getGamepadModeOrCreateDesktop(device))
                .getMode();
    }

    public GpadOsActionModule getCurrentModeFor(GamepadDevice device) {
        return moduleMap.get(getCurrentModeNameFor(device));
    }

    public List<GamepadDto> getAllGamepads() {
        return gamepadModeDao.getAllGamepads();
    }

    public GpadOsActionModule switchCurrentMode(String mode, GamepadDevice device) {
        stateService.nullifyForced();
        stateService.recognizeScene();
        gamepadModeDao.setModeFor(device).accept(mode);
        return moduleMap.get(mode);
    }

    public GpadOsActionModule getDesktopModule() {
        return moduleMap.get("DESKTOP");
    }

    public List<ModeVto> getAllModes() {
        List<ModeVto> allModes = modeDao.getAllModes();

        return moduleMap.keySet()
                .stream()
                .map(q -> ModeVto.builder()
                        .id(allModes.stream().filter(m -> m.getAdapterMode().equals(q)).findFirst().orElseGet(ModeVto.builder()::build).getId())
                        .adapterMode(q)
                        .scenic(moduleMap.get(q).isScenic())
                        .keyEvtTypes(moduleMap.get(q).getVerbs())
                        .nouns(moduleMap.get(q).getNouns())
                        .build())
                .toList();
    }

    public Collection<String> getModeVerbs(String mode) {
        return Optional.ofNullable(moduleMap.get(mode))
                .map(GpadOsActionModule::getVerbs).stream()
                .flatMap(Collection::stream)
                .collect(Collectors.collectingAndThen(Collectors.toCollection(HashSet::new), funky(q -> q.addAll(modeDao.getModeVerbs(mode)))));
    }

    public List<String> getModeNouns(String mode) {
        return Optional.ofNullable(moduleMap.get(mode))
                .map(GpadOsActionModule::getNouns)
                .orElseGet(() -> modeDao.getModeVerbs(mode));
    }
}
