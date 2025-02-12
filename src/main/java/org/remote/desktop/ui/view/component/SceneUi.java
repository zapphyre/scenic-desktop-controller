package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import org.remote.desktop.model.ActionVto;
import org.remote.desktop.model.SceneVto;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class SceneUi extends VerticalLayout {

    private final Supplier<Collection<SceneVto>> allScenes;

    VerticalLayout inheritedActions;
    VerticalLayout newlyAddedActions;
    VerticalLayout ownActions;
    VerticalLayout actions;

    Select<SceneVto> inheritsFrom;

    public SceneUi(Supplier<Collection<SceneVto>> allScenes) {
        this.allScenes = allScenes;

        inheritedActions = new VerticalLayout();
        newlyAddedActions = new VerticalLayout();
        ownActions = new VerticalLayout();
        actions = new VerticalLayout();

        inheritsFrom = new Select<>("Inherits from",
                e -> currentVto.get().setInherits(e.getValue()));
        inheritsFrom.setItems(allScenes.get());

        inheritsFrom.setItemLabelGenerator(SceneVto::getName);
        inheritsFrom.addValueChangeListener(e -> {
            inheritedActions.removeAll();
            scrapeActionsRecursive(currentVto.get()).stream()
                    .map(q -> new ActionDefUi(q, allScenes, false, p -> currentVto.get().getActions().remove(p)))
                    .forEach(inheritedActions::add);
        });

        Button addAction = new Button("New Action");
        addAction.addClickListener(e -> {
            ActionVto actionVto = new ActionVto();
            SceneVto sceneVto = currentVto.get();
            sceneVto.getActions().add(actionVto);
            newlyAddedActions.add(new ActionDefUi(actionVto, allScenes, q -> sceneVto.getActions().remove(q)));
        });

        HorizontalLayout selectAbnButtonHoriz = new HorizontalLayout(inheritsFrom, addAction);
        selectAbnButtonHoriz.setAlignItems(Alignment.BASELINE);

        actions.add(newlyAddedActions, ownActions, inheritedActions);
        add(selectAbnButtonHoriz, actions);
    }

    Supplier<SceneVto> currentVto = SceneVto::new;

    public Component render(SceneVto sceneVto) {
        currentVto = () -> sceneVto;
        newlyAddedActions.removeAll();
        ownActions.removeAll();
        inheritedActions.removeAll();

        if (sceneVto.getInherits() != null)
            inheritsFrom.setValue(sceneVto.getInherits());

        sceneVto.getActions().stream()
                .map(q -> new ActionDefUi(q, allScenes, true, p -> sceneVto.getActions().remove(p)))
                .forEach(ownActions::add);

        scrapeActionsRecursive(sceneVto.getInherits()).stream()
                .map(p -> new ActionDefUi(p, allScenes, false, o -> {
                }))
                .forEach(inheritedActions::add);

        return this;
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
