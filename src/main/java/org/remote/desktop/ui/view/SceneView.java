package org.remote.desktop.ui.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.remote.desktop.event.SceneStateRepository;
import org.remote.desktop.model.vto.SceneVto;

import java.util.Optional;

import static org.remote.desktop.ui.view.SourcesManagerView.uiReadyCb;

@UIScope
@PageTitle("Manage Scene State")
@Route(value = "scene")
@SpringComponent
public class SceneView extends VerticalLayout {

    public SceneView(SceneStateRepository sceneStateRepository) {
        HorizontalLayout lay = new HorizontalLayout();
        HorizontalLayout recognized = new HorizontalLayout();
        HorizontalLayout forced = new HorizontalLayout();

        renderSceneName(recognized, "Last Recognized Scene", sceneStateRepository.getLastSceneNameRecorded());
        renderSceneName(forced, "Forced Scene", Optional.ofNullable(sceneStateRepository.getForcedScene()).map(SceneVto::getWindowName).orElse("[not forced]"));

        Button clearBtn = new Button("Clear Forced Scene", q -> sceneStateRepository.nullifyForcedScene());
        clearBtn.addClickListener(event -> uiReadyCb(this, () -> renderSceneName(forced, "Forced Scene", "[reset]")));

        lay.add(recognized, new Text(";;"), forced, clearBtn);

        sceneStateRepository.registerRecognizedSceneObserver(q -> uiReadyCb(this, () -> renderSceneName(recognized, "Last Recognized Scene", q)));
        sceneStateRepository.registerForcedSceneObserver(q -> uiReadyCb(this, () -> renderSceneName(forced, "Forced Scene", q)));

        lay.setAlignItems(Alignment.CENTER);
        add(lay);
    }

    void renderSceneName(HorizontalLayout layout, String title, String name) {
        layout.removeAll();
        layout.add(new Text("%s: %s".formatted(title, Optional.ofNullable(name).orElse("[unknown]"))));
    }
}
