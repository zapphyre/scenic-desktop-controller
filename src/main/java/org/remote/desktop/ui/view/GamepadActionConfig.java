package org.remote.desktop.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.dataview.ComboBoxListDataView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.remote.desktop.model.SceneVto;
import org.remote.desktop.service.SceneService;
import org.remote.desktop.ui.view.component.SceneDialog;
import org.remote.desktop.ui.view.component.SceneUi;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@UIScope
@PageTitle("Gamepad Configurer UI")
@Route(value = "config")
@SpringComponent
public class GamepadActionConfig extends VerticalLayout {

    private ComboBox<SceneVto> allScenes = new ComboBox<>("Scene");
    private VerticalLayout selectedScene = new VerticalLayout();

    private SceneVto selected;

    public GamepadActionConfig(SceneService sceneService) {
        List<SceneVto> scenes = new LinkedList<>(sceneService.getScenes());

        ComboBoxListDataView<SceneVto> dataProv = allScenes.setItems(scenes);
        SceneUi sceneUi = new SceneUi(() -> dataProv.getItems().toList());
        allScenes.setItemLabelGenerator(SceneVto::getName);
        allScenes.addValueChangeListener(e -> {
            selectedScene.removeAll();
            selected = e.getValue();
            selectedScene.add(sceneUi.render(e.getValue()));
        });

        Button newScene = new Button("New Scene");
        newScene.addClickListener(e -> new SceneDialog(scenes, q -> {
            scenes.add(sceneService.save(q));
            dataProv.refreshAll();
            allScenes.setValue(q);
        }).open());

        Button edit = new Button("Edit");
        edit.addClickListener(e -> {
            new SceneDialog(selected, scenes, q -> {
                scenes.removeIf(s -> Objects.equals(s.getId(), selected.getId()));
                scenes.add(sceneService.save(q));
                dataProv.refreshAll();
            }, p -> {
                scenes.removeIf(s -> Objects.equals(s.getId(), selected.getId()));
                dataProv.refreshAll();
            }).open();
        });


        Button saveAll = new Button("Save All", q -> {
            List<SceneVto> saved = sceneService.saveAll(scenes);
            scenes.clear();
            scenes.addAll(saved);
            dataProv.refreshAll();
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(allScenes, newScene, edit, saveAll);
        horizontalLayout.setAlignItems(Alignment.BASELINE);

        add(horizontalLayout, selectedScene);
    }
}
