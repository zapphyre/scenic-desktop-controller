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
    void getSceneByWindowNameTest() {
        Scene s = createScene("/ X", "x");
        Scene empty = createScene("", "Base");

        sceneRepository.save(s);
        sceneRepository.save(empty);

        List<Scene> rogan = sceneRepository.findBySceneContain("Home / X — Mozilla Firefox");

        Assertions.assertFalse(rogan.isEmpty());
    }

    public static Scene createScene(String wName, String sName, GamepadEvent...gpadEvents) {
        return Scene.builder()
                .windowName(wName)
                .name(sName)
                .gamepadEvents(Arrays.asList(gpadEvents))
                .build();
    }


}
