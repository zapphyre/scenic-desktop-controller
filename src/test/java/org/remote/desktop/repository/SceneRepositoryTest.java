package org.remote.desktop.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.remote.desktop.entity.Scene;
import org.remote.desktop.mapper.CycleAvoidingMappingContext;
import org.remote.desktop.mapper.SceneMapper;
import org.remote.desktop.model.GPadEventVto;
import org.remote.desktop.model.SceneVto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest(properties = {
        """
                spring.datasource.url=jdbc:h2:file:./scene_test
                spring.datasource.driverClassName=org.h2.Driver
                spring.datasource.username=sa
                spring.datasource.password=password
                spring.jpa.defer-datasource-initialization=true
                spring.jpa.hibernate.ddl-auto=create
                """
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SceneRepositoryTest {

    @Autowired
    private SceneRepository sceneRepository;
    private SceneMapper sceneMapper = Mappers.getMapper(SceneMapper.class);

    @Test
    void testInheritsOneToOneRelationship() {
        SceneVto scene1vto = SceneVto.builder()
                .name("qwer")
                .windowName("windowName")
                .build();

        Scene e1 = sceneMapper.map(scene1vto, new CycleAvoidingMappingContext());
        Scene saveScene1 = sceneRepository.save(e1);

        SceneVto scene2Vto = SceneVto.builder()
                .name("asdf")
                .windowName("windowName")
//                .inherits(scene1vto.toBuilder().id(saveScene1.getId()).build())
                .actions(List.of(GPadEventVto.builder()
//                        .nextScene(scene1vto.toBuilder().id(saveScene1.getId()).build())
                        .build()))
                .build();

        Scene scene2 = sceneMapper.map(scene2Vto, new CycleAvoidingMappingContext());
        Scene e2saved = sceneRepository.save(scene2);

        SceneVto scene3Vto = SceneVto.builder()
                .name("xzcv")
                .windowName("windowName")
//                .inherits(scene1vto.toBuilder().id(saveScene1.getId()).build())
                .actions(List.of(GPadEventVto.builder()
//                        .nextScene(scene1vto.toBuilder().id(saveScene1.getId()).build())
                        .build()))
                .build();

        Scene e3 = sceneMapper.map(scene3Vto, new CycleAvoidingMappingContext());
        try {
            Scene e3saved = sceneRepository.save(e3);
        } catch (Exception w) {
            System.out.println(w.getMessage());
        }

        SceneVto scene4vto = SceneVto.builder()
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
}
