package org.remote.desktop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asmus.model.EButtonAxisMapping;
import org.junit.jupiter.api.Test;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.dto.GamepadEventDto;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.model.dto.XdoActionDto;

import java.util.List;

public class SceneCtrlTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void canMapScene() throws JsonProcessingException {
        String request = """
                {"name":"x","windowName":"/ X","inherits":{"name":"Base","windowName":"","inherits":{"name":"system","windowName":"","inherits":null,"leftAxisEvent":"MOUSE","rightAxisEvent":"SCROLL","gamepadEvents":[]},"leftAxisEvent":"MOUSE","rightAxisEvent":"SCROLL","gamepadEvents":[]},"leftAxisEvent":"INHERITED","rightAxisEvent":"SCROLL","gamepadEvents":[]}
                """;

        SceneDto sceneDto = objectMapper.readValue(request, SceneDto.class);
    }

    @Test
    void canSerializeCyclicGraph() throws JsonProcessingException {
        SceneDto inherits = SceneDto.builder()
                .name("inherited")
                .windowName("inherited")
                .gamepadEvents(List.of())
                .build();

        SceneDto nextScene = SceneDto.builder()
                .name("next")
                .windowName("nextScene")
                .gamepadEvents(List.of())
                .build();

        XdoActionDto xdo = XdoActionDto.builder()
                .id(3L)
                .keyEvt(EKeyEvt.STROKE)
                .build();

        GamepadEventDto eventDto = GamepadEventDto.builder()
                .id(4L)
                .trigger(EButtonAxisMapping.A)
                .nextScene(nextScene)
                .actions(List.of(xdo))
                .build();

        xdo.setGamepadEvent(eventDto);

        SceneDto sceneDto = SceneDto.builder()
                .name("Base")
                .windowName("Base")
                .inherits(inherits)
                .gamepadEvents(List.of(eventDto))
                .build();

//        objectMapper.enable(DeserializationFeature.USE_LONG_FOR_INTS); // Treat int JSON numbers as Long
        String scene = objectMapper.writeValueAsString(sceneDto);
        System.out.println(scene);

        XdoActionDto first = sceneDto.getGamepadEvents().getFirst().getActions().getFirst();
        String firstStr = objectMapper.writeValueAsString(first);

        XdoActionDto xdoActionDto = objectMapper.readValue(firstStr, XdoActionDto.class);

        SceneDto sceneDto1 = objectMapper.readValue(scene, SceneDto.class);
    }
}
