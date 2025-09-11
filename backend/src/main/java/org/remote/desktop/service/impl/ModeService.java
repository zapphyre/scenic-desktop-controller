package org.remote.desktop.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.desktop.remote.mode.GpadOsActionModule;
import org.remote.desktop.db.dao.ModeDao;
import org.remote.desktop.model.vto.ModeVto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModeService {

    private final ModeDao modeDao;
    private final StateService stateService;
    private final Map<String, GpadOsActionModule> moduleMap;

    @Getter @Setter
    private GpadOsActionModule currentMode;

    @PostConstruct
    void init() {
        currentMode = getDesktopModule();
    }

    public void switchCurrentMode(String mode) {
        currentMode = moduleMap.get(mode);
        stateService.nullifyForced();
    }

    public GpadOsActionModule getDesktopModule() {
        return moduleMap.get("DESKTOP");
    }

    public List<ModeVto> getAllModes() {
        return modeDao.getAllModes();
    }

    public List<String> getModeVerbs(String mode) {
        return Optional.ofNullable(moduleMap.get(mode))
                .map(GpadOsActionModule::getVerbs)
                .orElseGet(() -> modeDao.getModeVerbs(mode));
    }

    public List<String> getModeNouns(String mode) {
        return Optional.ofNullable(moduleMap.get(mode))
                .map(GpadOsActionModule::getNouns)
                .orElseGet(() -> modeDao.getModeVerbs(mode));
    }
}
