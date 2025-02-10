package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.remote.desktop.model.ActionVdo;
import org.remote.desktop.model.SceneVdo;

import java.util.List;

public class SceneUi extends VerticalLayout {

    private final SceneVdo scene;

    public SceneUi(SceneVdo scene, List<SceneVdo> allScenes) {
        this.scene = scene;

        ComboBox<SceneVdo> inheritsFrom = new ComboBox<>("Inherits from");
        inheritsFrom.setItems(allScenes);
        inheritsFrom.setItemLabelGenerator(SceneVdo::getName);
        inheritsFrom.addValueChangeListener(e -> scene.setInherits(e.getValue()));

        Button addAction = new Button("New Action");
        addAction.addClickListener(e -> {
            ActionVdo actionVdo = new ActionVdo();
            scene.getActions().add(actionVdo);
            add(new ActionDefUi(actionVdo, q -> scene.getActions().remove(q)));
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(inheritsFrom, addAction);
        horizontalLayout.setAlignItems(Alignment.BASELINE);

        add(horizontalLayout);
        scene.getActions().stream()
                .map(q -> new ActionDefUi(q, p -> scene.getActions().remove(p)))
                .forEach(this::add);
    }
}
