package org.remote.desktop.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.remote.desktop.db.entity.GamepadEvent;
import org.remote.desktop.db.entity.Scene;
import org.remote.desktop.db.repository.SceneRepository;
import org.remote.desktop.mapper.CycleAvoidingMappingContext;
import org.remote.desktop.mapper.SceneMapper;
import org.remote.desktop.model.dto.GamepadEventDto;
import org.remote.desktop.model.dto.SceneDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SceneRepositoryTest {

    @Autowired
    private SceneRepository sceneRepository;
    private SceneMapper sceneMapper = Mappers.getMapper(SceneMapper.class);

    @Test
    void testInheritsOneToOneRelationship() {
        SceneDto scene1vto = SceneDto.builder()
                .name("qwer")
                .windowName("windowName")
                .build();

        Scene e1 = sceneMapper.map(scene1vto, new CycleAvoidingMappingContext());
        Scene saveScene1 = sceneRepository.save(e1);

        SceneDto scene2Vto = SceneDto.builder()
                .name("asdf")
                .windowName("windowName")
                .inherits(scene1vto)
                .gamepadEvents(List.of(GamepadEventDto.builder()
                        .nextScene(scene1vto)
                        .build()))
                .build();

        Scene scene2 = sceneMapper.map(scene2Vto, new CycleAvoidingMappingContext());
        Scene e2saved = sceneRepository.save(scene2);

        SceneDto scene3Vto = SceneDto.builder()
                .name("xzcv")
                .windowName("windowName")
                .inherits(scene1vto)
                .gamepadEvents(List.of(GamepadEventDto.builder()
                        .nextScene(scene1vto)
                        .build()))
                .build();

        Scene e3 = sceneMapper.map(scene3Vto, new CycleAvoidingMappingContext());
        try {
            Scene e3saved = sceneRepository.save(e3);
        } catch (Exception w) {
            System.out.println(w.getMessage());
        }

        SceneDto scene4vto = SceneDto.builder()
                .name("opiu")
                .windowName("windowName")
                .build();

        Scene e4 = sceneMapper.map(scene4vto, new CycleAvoidingMappingContext());
        Scene e4saved = sceneRepository.save(e4);

        sceneRepository.flush();

        Scene dvojka = sceneRepository.findById(scene2.getName()).orElseThrow();
//
        Assertions.assertEquals("asdf", dvojka.getName());
        Assertions.assertEquals(e1.getName(), dvojka.getInherits().getName());

        List<Scene> all = sceneRepository.findAll();

        sceneRepository.flush();
    }

    @Test
    void getSceneByWindowNameTest() {
        Scene s = createScene("/ X", "x");
        Scene empty = createScene("", "Base");

        sceneRepository.save(s);
        sceneRepository.save(empty);

        List<Scene> rogan = sceneRepository.findBySceneContain("Home / X — Mozilla Firefox");

        Assertions.assertFalse(rogan.isEmpty());
    }

    Scene createScene(String wName, String sName, GamepadEvent...gpadEvents) {
        return Scene.builder()
                .windowName(wName)
                .name(sName)
                .gamepadEvents(Arrays.asList(gpadEvents))
                .build();
    }


}
