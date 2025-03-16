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

}
