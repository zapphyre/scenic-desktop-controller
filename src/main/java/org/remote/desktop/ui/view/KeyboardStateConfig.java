package org.remote.desktop.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.remote.desktop.component.SourceManager;
import org.remote.desktop.event.KeyboardStateRepository;
import org.remote.desktop.event.SceneStateRepository;
import org.remote.desktop.model.event.XdoCommandEvent;

import java.util.Optional;

@UIScope
@PageTitle("Keyboard state UI")
@Route(value = "state")
@SpringComponent
public class KeyboardStateConfig extends VerticalLayout {

    private final KeyboardStateRepository stateRepository;

    public KeyboardStateConfig(KeyboardStateRepository stateRepository,
                               SceneStateRepository sceneStateRepository,
                               SourceManager sourceManager) {
        this.stateRepository = stateRepository;

        HorizontalLayout pressedButtons = new HorizontalLayout();
        HorizontalLayout sceneName = new HorizontalLayout();

        sceneName.add(new Text(sceneStateRepository.getLastSceneNameRecorded()));

        sceneStateRepository.registerSceneObserver(q -> uiReadyCb(() -> {
            sceneName.removeAll();
            sceneName.add(new Text(q));
        }));

        stateRepository.getPressedKeys().forEach(key -> pressedButtons.add(togglePressButton(key)));

        stateRepository.registerXdoCommandObserver(q -> uiReadyCb(() -> {
            pressedButtons.removeAll();
            stateRepository.getPressedKeys()
                    .forEach(key -> pressedButtons.add(togglePressButton(key)));
        }));

        add(sceneName, pressedButtons);

        VerticalLayout sourceStateSection = new VerticalLayout();

        rerenderConnectionSection(sourceManager, sourceStateSection);

        add(sourceStateSection);
    }

    void rerenderConnectionSection(SourceManager sourceManager, VerticalLayout sourceStateSection) {
        sourceStateSection.removeAll();

        sourceStateSection.add(new HorizontalLayout(new Text("Is Available?"), new Text("Toggle State"), new Text("Is Connected?")));
        sourceManager.getConnectableSources().forEach(q -> {
            HorizontalLayout sourcesRow = new HorizontalLayout();
            sourcesRow.setAlignItems(Alignment.CENTER);

            Checkbox avail = new Checkbox(q.isAvailable());
            avail.setEnabled(false);
            Button toggleSourceStateBtn = new Button(q.describe());
            toggleSourceStateBtn.addClickListener(e -> {
                if (sourceManager.toggleSourceConnection(q))
                    rerenderConnectionSection(sourceManager, sourceStateSection);
            });
            Checkbox state = new Checkbox(sourceManager.isConnected(q));
            state.setEnabled(false);

            sourcesRow.add(avail, toggleSourceStateBtn, state);
            sourceStateSection.add(sourcesRow);
        });
    }

    Button togglePressButton(XdoCommandEvent key) {
        return new Button(key.getKeyPart().getKeyPress() + " [down]", q -> stateRepository.issueKeyupCommand(key));
    }

    void uiReadyCb(Runnable callback) {
        getUI().flatMap(ui -> Optional.of(ui).filter(Component::isAttached)).ifPresent(u -> {
            u.access((Command) callback::run);
        });
    }
}
