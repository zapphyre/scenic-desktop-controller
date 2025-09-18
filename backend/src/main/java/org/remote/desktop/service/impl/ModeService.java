package org.remote.desktop.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.desktop.remote.mode.GpadOsActionModule;
import org.remote.desktop.db.dao.ModeDao;
import org.remote.desktop.model.vto.ModeVto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.zapphyre.function.FunHelper.funky;

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

    public GpadOsActionModule switchCurrentMode(String mode) {
        stateService.nullifyForced();
        stateService.recognizeScene();
        return currentMode = moduleMap.get(mode);
    }

    public GpadOsActionModule getDesktopModule() {
        return moduleMap.get("DESKTOP");
    }

    public List<ModeVto> getAllModes() {
        List<ModeVto> allModes = modeDao.getAllModes();
        return moduleMap.keySet()
                .stream()
                .map(q -> ModeVto.builder()
                        .id(allModes.stream().filter(m -> m.getAdapterMode().equals(q)).findFirst().orElse(ModeVto.builder().build()).getId())
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
