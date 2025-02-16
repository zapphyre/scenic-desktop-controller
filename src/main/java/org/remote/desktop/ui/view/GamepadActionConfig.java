package org.remote.desktop.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.dataview.ComboBoxListDataView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.remote.desktop.component.ReplaceableSet;
import org.remote.desktop.component.SceneDbToolbox;
import org.remote.desktop.model.SceneVto;
import org.remote.desktop.service.SceneService;
import org.remote.desktop.ui.view.component.SaveNotifiaction;
import org.remote.desktop.ui.view.component.SceneDialog;
import org.remote.desktop.ui.view.component.SceneUi;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;


@UIScope
@PageTitle("Gamepad Configurer UI")
@Route(value = "config")
@SpringComponent
public class GamepadActionConfig extends VerticalLayout {

    private ComboBox<SceneVto> allScenes = new ComboBox<>("Scene");
    private VerticalLayout selectedScene = new VerticalLayout();
    ReplaceableSet<SceneVto> scenes;
    private final SceneService sceneService;
    private SceneVto selected;
    Checkbox autoSave;
    ComboBoxListDataView<SceneVto> dataProv;

    public GamepadActionConfig(SceneDbToolbox dbToolbox, SceneService sceneService) {
        scenes = new ReplaceableSet<>(sceneService.getScenes());
        this.sceneService = sceneService;

        dataProv = allScenes.setItems(scenes);

        allScenes.setItemLabelGenerator(SceneVto::getName);
        allScenes.addValueChangeListener(e -> {
            selectedScene.removeAll();
            selectedScene.add(new SceneUi(dbToolbox, selected = e.getValue(), () -> dataProv.getItems().toList()));
        });

        Button newScene = new Button("New Scene");
        newScene.addClickListener(e -> new SceneDialog(scenes, this::saveSceneCallback).open());

        Button edit = new Button("Edit");
        edit.addClickListener(e -> {
            new SceneDialog(selected, scenes, this::saveSceneCallback, p -> {
//                scenes.removeIf(s -> Objects.equals(s.getId(), selected.getId()));
                dataProv.refreshAll();
            }).open();
        });

        Button saveAll = new Button("Save All", q -> {
//            List<SceneVto> saved = sceneService.saveAll(scenes);
//            scenes.clear();
//            scenes.addAll(saved);
            scenes.replaceAll(sceneService.saveAll(scenes));
            dataProv.refreshAll();
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(allScenes, newScene, edit);
        autoSave = new Checkbox("Auto save", true);
        autoSave.addValueChangeListener(e -> {
            if (e.getValue())
                horizontalLayout.remove(saveAll);
            else
                horizontalLayout.add(saveAll);
        });

        horizontalLayout.add(autoSave);
        horizontalLayout.setAlignItems(Alignment.BASELINE);

        add(horizontalLayout, selectedScene);
    }

    void saveSceneCallback(SceneVto sceneVto) {
        if (!autoSave.getValue()) return;

        try {
            scenes.replace(sceneService.save(sceneVto));
            dataProv.refreshAll();
            allScenes.setValue(sceneVto);
            SaveNotifiaction.success();
        } catch (Exception e) {
            SaveNotifiaction.error();
        }
    }
}
