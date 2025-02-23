package org.remote.desktop.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.remote.desktop.event.KeyboardStateRepository;
import org.remote.desktop.event.SceneStateRepository;
import org.remote.desktop.model.event.XdoCommandEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@UIScope
@PageTitle("Keyboard state UI")
@Route(value = "state")
@SpringComponent
public class KeyboardStateConfig extends VerticalLayout {

    private final KeyboardStateRepository stateRepository;

    public KeyboardStateConfig(KeyboardStateRepository stateRepository, SceneStateRepository sceneStateRepository) {
        this.stateRepository = stateRepository;

        HorizontalLayout pressedButtons = new HorizontalLayout();
        Map<XdoCommandEvent, Button> pressedButtonMap = new HashMap<>();
        HorizontalLayout sceneName = new HorizontalLayout();

        sceneName.add(new Text(sceneStateRepository.getLastSceneNameRecorded()));

        sceneStateRepository.registerSceneObserver(q -> uiReadyCb(() -> {
            sceneName.removeAll();
            sceneName.add(new Text(q));
        }));

        stateRepository.getPressedKeys().forEach(key -> add(togglePressButton(key)));

        stateRepository.registerXdoCommandObserver(q -> uiReadyCb(() -> {
            if (pressedButtonMap.containsKey(q.invert()))
                pressedButtons.remove(pressedButtonMap.remove(q.invert()));
            else
                pressedButtons.add(pressedButtonMap.computeIfAbsent(q, this::togglePressButton));
        }));

        add(sceneName, pressedButtons);
    }

    Button togglePressButton(XdoCommandEvent key) {
        return new Button(key.getKeyPart().getKeyPress() + " [down]", q -> stateRepository.onApplicationEvent(key));
    }

    void uiReadyCb(Runnable callback) {
        getUI().flatMap(ui -> Optional.of(ui).filter(Component::isAttached)).ifPresent(u -> {
            u.access((Command) callback::run);
        });
    }
}
