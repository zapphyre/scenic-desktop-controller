package org.remote.desktop;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.entity.GamepadEvent;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.db.entity.XdoAction;
import org.remote.desktop.db.repository.GamepadEventRepository;
import org.remote.desktop.db.repository.SceneRepository;
import org.remote.desktop.db.repository.XdoActionRepository;
import org.remote.desktop.mapper.CycleAvoidingMappingContext;
import org.remote.desktop.mapper.SceneMapper;
import org.remote.desktop.model.dto.SceneDto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.SERIALIZABLE)
@SpringBootApplication
@RequiredArgsConstructor
public class GamepadDesktopController {

    private final SceneRepository sceneRepository;
    private final XdoActionRepository actionRepository;
    private final GamepadEventRepository gamepadEventRepository;

    private final SceneMapper sceneMapper;
    private final ObjectMapper objectMapper;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        SpringApplication.run(GamepadDesktopController.class, args);
    }

    //    @PostConstruct
    void read() throws IOException {
        String readString = Files.readString(Paths.get("dump.json"));
        SceneDto[] sceneDtos1 = objectMapper.readValue(readString, SceneDto[].class);

        List<SceneDto> list = Arrays.asList(sceneDtos1);
        list.forEach(sceneDto -> {

            sceneDto.getGamepadEvents().forEach(gamepadEvent -> {
                gamepadEvent.setScene(sceneDto);
            });
        });

        Map<String, Long> sceneNameIdMap = list.stream().collect(Collectors.toMap(SceneDto::getName, SceneDto::getId));

        List<Scene> scenes = sceneMapper.mapDtos(list, new CycleAvoidingMappingContext());

        List<XdoAction> allActions = scenes.stream()
                .flatMap(q -> q.getGamepadEvents().stream())
                .flatMap(q -> q.getActions().stream())
                .toList();

        allActions.forEach(action -> action.setGamepadEvent(null));
        actionRepository.saveAll(allActions);

        List<GamepadEvent> list1 = scenes.stream()
                .flatMap(q -> q.getGamepadEvents().stream())
                .toList();

        list1.forEach(q -> {
            q.setScene(null);
            q.setNextScene(null);
        });
        gamepadEventRepository.saveAll(list1);

        scenes.forEach(q -> {
            q.setGamepadEvents(null);
            q.setInherits(null);
        });
        sceneRepository.saveAll(scenes);

        List<Scene> again = sceneMapper.mapDtos(list, new CycleAvoidingMappingContext());
        again.forEach(sceneDto -> {

            sceneDto.getGamepadEvents().forEach(gamepadEvent -> {
                gamepadEvent.setScene(sceneDto);
            });
        });
        List<XdoAction> actionList = again.stream()
                .flatMap(q -> q.getGamepadEvents().stream())
                .flatMap(q -> q.getActions().stream())
                .toList();
        actionRepository.saveAll(actionList);

        again.forEach(q -> {
            if (q.getInherits() != null) {
                Long id = q.getInherits().getId();

                Scene found = scenes.stream()
                        .filter(p -> p.getId().equals(id))
                        .findFirst()
                        .orElseThrow();

                Scene root = scenes.stream()
                        .filter(s -> s.getId().equals(q.getId()))
                        .findFirst()
                        .orElseThrow();

                root.setInherits(found);
            }
        });
        sceneRepository.saveAll(scenes);

        List<GamepadEvent> gamepadEventList = again.stream()
                .flatMap(q -> q.getGamepadEvents().stream())
                .toList();

        list1.forEach(q -> {
            GamepadEvent template = gamepadEventList.stream()
                    .filter(e -> e.getId().equals(q.getId()))
                    .findFirst()
                    .orElseThrow();

            Scene parentScene = scenes.stream()
                    .filter(e -> e.getId().equals(template.getScene().getId()))
                    .findFirst()
                    .orElseThrow();

            if (template.getNextScene() != null) {
                Scene nextsc = template.getNextScene();
                Long l = sceneNameIdMap.get(nextsc.getName());
                nextsc.setId(l);
                Scene nextScene = scenes.stream()
                        .filter(e -> e.getId().equals(l))
                        .findFirst()
                        .orElseThrow();
                q.setNextScene(nextScene);
            }

            q.setScene(parentScene);
        });
//        sceneRepository.saveAll(scenes);
        gamepadEventRepository.saveAll(list1);
    }

//    @PostConstruct
    void init() throws IOException {
        List<Scene> all = sceneRepository.findAll();

        List<SceneDto> map = sceneMapper.map(all, new CycleAvoidingMappingContext());
        String string = objectMapper.writeValueAsString(map);

        SceneDto[] back = objectMapper.readValue(string, SceneDto[].class);
        List<SceneDto> sceneDtos = assignIdsToDtos(Arrays.asList(back));

        String qqq = objectMapper.writeValueAsString(sceneDtos);
        Files.writeString(Paths.get("dump.json"), qqq);

        String readString = Files.readString(Paths.get("dump.json"));
        SceneDto[] sceneDtos1 = objectMapper.readValue(readString, SceneDto[].class);
    }

    public static List<SceneDto> assignIdsToDtos(List<SceneDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }

        // Map to memorize SceneDtos by name with their assigned IDs
        Map<String, Long> nameToIdMap = new HashMap<>();
        AtomicLong idGenerator = new AtomicLong(1);

        // Step 1: Assign IDs to root-level DTOs and memorize by name
        for (SceneDto dto : dtos) {
            nameToIdMap.computeIfAbsent(dto.getName(), k -> idGenerator.getAndIncrement());
        }

        // Step 2: Process each DTO’s inherits chain deeply, building new DTOs
        Map<SceneDto, SceneDto> dtoToNewDtoMap = new HashMap<>();
        for (SceneDto dto : dtos) {
            processInheritsChainDeeply(dto, dtoToNewDtoMap, nameToIdMap);
        }

        // Step 3: Build result list in input order
        List<SceneDto> result = new ArrayList<>(dtos.size());
        for (SceneDto dto : dtos) {
            result.add(dtoToNewDtoMap.get(dto));
        }

        return result;
    }

    private static void processInheritsChainDeeply(SceneDto dto, Map<SceneDto, SceneDto> dtoToNewDtoMap,
                                                   Map<String, Long> nameToIdMap) {
        if (dto == null || dtoToNewDtoMap.containsKey(dto)) {
            return; // Skip null or already processed DTOs
        }

        // Recursively process the inherits chain all the way down
        SceneDto newInherits = null;
        if (dto.getInherits() != null) {
            processInheritsChainDeeply(dto.getInherits(), dtoToNewDtoMap, nameToIdMap);
            newInherits = dtoToNewDtoMap.get(dto.getInherits());
            // If inherits isn’t processed yet, ensure it’s in the map by name
            if (newInherits == null) {
                Long inheritsId = nameToIdMap.get(dto.getInherits().getName());
                if (inheritsId == null) {
                    // If not in root list, assign from current DTO’s ID (shared chain logic)
                    inheritsId = nameToIdMap.get(dto.getName());
                    nameToIdMap.put(dto.getInherits().getName(), inheritsId);
                }
                newInherits = SceneDto.builder()
                        .id(inheritsId)
                        .name(dto.getInherits().getName())
                        .windowName(dto.getInherits().getWindowName())
                        .leftAxisEvent(dto.getInherits().getLeftAxisEvent())
                        .rightAxisEvent(dto.getInherits().getRightAxisEvent())
                        .gamepadEvents(new LinkedList<>(dto.getInherits().getGamepadEvents()))
                        .build();
                dtoToNewDtoMap.put(dto.getInherits(), newInherits);
                // Recurse again to ensure full chain is processed
                processInheritsChainDeeply(dto.getInherits(), dtoToNewDtoMap, nameToIdMap);
            }
        }

        // Get the ID from the nameToIdMap (assigned at root level)
        Long id = nameToIdMap.get(dto.getName());
        if (id == null) {
            // Shouldn’t happen for input DTOs, but handle edge case
            throw new IllegalStateException("No ID assigned for DTO with name: " + dto.getName());
        }

        // Create the new DTO with the reused ID and linked inherits
        SceneDto newDto = SceneDto.builder()
                .id(id) // Always reuse the root-assigned ID
                .name(dto.getName())
                .windowName(dto.getWindowName())
                .inherits(newInherits)
                .leftAxisEvent(dto.getLeftAxisEvent())
                .rightAxisEvent(dto.getRightAxisEvent())
                .gamepadEvents(new LinkedList<>(dto.getGamepadEvents()))
                .build();
        dtoToNewDtoMap.put(dto, newDto);
    }


}
