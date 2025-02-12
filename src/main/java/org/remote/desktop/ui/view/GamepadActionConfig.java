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

@UIScope
@PageTitle("Gamepad Configurer UI")
@Route(value = "config")
@SpringComponent
public class GamepadActionConfig extends VerticalLayout {

    ComboBox<SceneVto> allScenes = new ComboBox<>("Scene");
    VerticalLayout selectedScene = new VerticalLayout();


    public GamepadActionConfig(SceneService sceneService) {
        List<SceneVto> scenes = new LinkedList<>(sceneService.getScenes());
//        ListDataProvider<SceneVto> dataProvider = new ListDataProvider<>(scenes);

//        dataProvider.addDataProviderListener(event ->
//                {
//                    System.out.println(event);
//                }
//                );

//        allScenes.setItems(scenes);

        ComboBoxListDataView<SceneVto> dataProv = allScenes.setItems(scenes);
        SceneUi sceneUi = new SceneUi(() -> dataProv.getItems().toList());
        allScenes.setItemLabelGenerator(SceneVto::getName);
        allScenes.addValueChangeListener(e -> {
            selectedScene.removeAll();
            selectedScene.add(sceneUi.render(e.getValue()));
        });

        Button newScene = new Button("New Scene");
        newScene.addClickListener(e -> new SceneDialog(scenes, q -> {
            dataProv.addItem(q);
//            allScenes.setItems(scenes);
            allScenes.setValue(q);
            allScenes.getDataProvider().refreshAll();
        }).open());

        Button saveAll = new Button("Save All", q ->
//                sceneService.saveAll(dataProv.getItems().toList()).forEach(dataProv::refreshItem)
        {
            List<SceneVto> saved = sceneService.saveAll(scenes);
            scenes.clear();
            scenes.addAll(saved);
            dataProv.refreshAll();
        }
        );

        HorizontalLayout horizontalLayout = new HorizontalLayout(allScenes, newScene, saveAll);
        horizontalLayout.setAlignItems(Alignment.BASELINE);

        add(horizontalLayout);
        add(selectedScene);
    }
}
