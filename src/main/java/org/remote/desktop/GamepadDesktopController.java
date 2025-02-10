package org.remote.desktop;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.entity.Action;
import org.remote.desktop.entity.XdoAction;
import org.remote.desktop.entity.Scene;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.repository.SceneRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.util.List;
import java.util.Set;

@EnableCaching
@SpringBootApplication
@RequiredArgsConstructor
public class GamepadDesktopController {

    private final SceneRepository sceneRepository;

    public static void main(String[] args) {
        SpringApplication.run(GamepadDesktopController.class, args);
    }

//    @Transactional
//    @PostConstruct
    public void init() {
        Scene scene = new Scene();
        scene.setWindowName(".java");
        scene.setName("idea");
        Action action = new Action();

        action.setTrigger("X");
        action.setModifiers(Set.of("A", "B"));

        XdoAction xdoAction = new XdoAction();
        xdoAction.setKeyEvt(EKeyEvt.STROKE);
        xdoAction.setKeyPress("j");

        action.setActions(List.of(xdoAction));
        scene.setActions(List.of(action));

        sceneRepository.save(scene);
        System.out.println("saved scene: " + scene);
    }
}
