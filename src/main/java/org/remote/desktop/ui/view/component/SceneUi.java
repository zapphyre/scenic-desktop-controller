package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import org.remote.desktop.model.ActionVto;
import org.remote.desktop.model.SceneVto;

import java.util.LinkedList;
import java.util.List;

public class SceneUi extends VerticalLayout {

    private final SceneVto scene;

    public SceneUi(SceneVto scene, List<SceneVto> allScenes) {
        this.scene = scene;

        VerticalLayout inheritedActions = new VerticalLayout();
        VerticalLayout newlyAddedActions = new VerticalLayout();
        VerticalLayout ownActions = new VerticalLayout();
        VerticalLayout actions = new VerticalLayout();

        Select<SceneVto> inheritsFrom = new Select<>("Inherits from", e -> scene.setInherits(e.getValue()));
        inheritsFrom.setItems(allScenes);

        if (scene.getInherits() != null)
            inheritsFrom.setValue(scene.getInherits());

        inheritsFrom.setItemLabelGenerator(SceneVto::getName);
        inheritsFrom.addValueChangeListener(e -> {
            inheritedActions.removeAll();
            List<ActionVto> actionVtos = scrapeActionsRecursive(scene);
//            VerticalLayout qwer = new VerticalLayout();
            actionVtos.stream()
                    .map(q -> new ActionDefUi(q, allScenes, false, p -> scene.getActions().remove(p)))
                    .forEach(inheritedActions::add);
//            inheritedActions.add(qwer);
        });

        Button addAction = new Button("New Action");
        addAction.addClickListener(e -> {
            ActionVto actionVto = new ActionVto();
            scene.getActions().add(actionVto);
            newlyAddedActions.add(new ActionDefUi(actionVto, allScenes, q -> scene.getActions().remove(q)));
        });

        HorizontalLayout selectAbnButtonHoriz = new HorizontalLayout(inheritsFrom, addAction);
        selectAbnButtonHoriz.setAlignItems(Alignment.BASELINE);

        scene.getActions().stream()
                .map(q -> new ActionDefUi(q, allScenes, true, p -> scene.getActions().remove(p)))
                .forEach(ownActions::add);

        scrapeActionsRecursive(scene.getInherits()).stream()
                .map(p -> new ActionDefUi(p, allScenes, false, o -> {
                }))
                .forEach(inheritedActions::add);

        actions.add(newlyAddedActions, ownActions, inheritedActions);

        add(selectAbnButtonHoriz, actions);
    }

    void render(SceneVto sceneVto) {

    }

    List<ActionVto> scrapeActionsRecursive(SceneVto sceneVto) {
        return sceneVto == null ? List.of() : scrapeActionsRecursive(sceneVto, new LinkedList<>());
    }

    List<ActionVto> scrapeActionsRecursive(SceneVto sceneVto, List<ActionVto> actionVtos) {
        if (sceneVto.getInherits() == null)
            return sceneVto.getActions();

        List<ActionVto> inherited = scrapeActionsRecursive(sceneVto.getInherits(), actionVtos);
        actionVtos.addAll(inherited);

        return actionVtos;
    }
}
