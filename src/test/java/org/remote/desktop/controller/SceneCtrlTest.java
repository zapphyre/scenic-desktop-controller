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
        SceneDto nextScene = SceneDto.builder()
                .name("next")
                .windowName("nextScene")
                .gamepadEvents(List.of())
                .build();

        XdoActionDto xdo = XdoActionDto.builder()
                .id(3)
                .keyEvt(EKeyEvt.STROKE)
                .build();

        GamepadEventDto eventDto = GamepadEventDto.builder()
                .id(4)
                .trigger(EButtonAxisMapping.A)
                .nextScene(nextScene)
                .actions(List.of(xdo))
                .build();

        xdo.setGamepadEvent(eventDto);

        SceneDto sceneDto = SceneDto.builder()
                .name("Base")
                .windowName("Base")
                .gamepadEvents(List.of(eventDto))
                .build();

        objectMapper.enable(DeserializationFeature.USE_LONG_FOR_INTS); // Treat int JSON numbers as Long
        String s = objectMapper.writeValueAsString(sceneDto);
        System.out.println(s);

        SceneDto sceneDto1 = objectMapper.readValue(s, SceneDto.class);
    }
}
