package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.data.SelectListDataView;
import org.remote.desktop.model.ActionVto;
import org.remote.desktop.model.SceneVto;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SceneUi extends VerticalLayout {

    private final Supplier<Collection<SceneVto>> allScenes;
    private final Consumer<SceneVto> chageCb;

    VerticalLayout inheritedActions;
    VerticalLayout newlyAddedActions;
    VerticalLayout ownActions;
    VerticalLayout actions;

    Select<SceneVto> inheritsFrom;
    SelectListDataView<SceneVto> items;

    public SceneUi(Supplier<Collection<SceneVto>> allScenes, Consumer<SceneVto> chageCb) {
        this.allScenes = allScenes;
        this.chageCb = chageCb;

        inheritedActions = new VerticalLayout();
        newlyAddedActions = new VerticalLayout();
        ownActions = new VerticalLayout();
        actions = new VerticalLayout();

        inheritsFrom = new Select<>("Inherits from",
                e -> currentVto.get().setInherits(e.getValue()));
        items = inheritsFrom.setItems(new LinkedList<>(allScenes.get()));

        inheritsFrom.setItemLabelGenerator(SceneVto::getName);
        inheritsFrom.addValueChangeListener(e -> {
            currentVto.get().setInherits(e.getValue());

            refreshInheritedSelectionList();
            chageCb.accept(currentVto.get());
        });

        Button removeInherits = new Button("✗", o -> {
            currentVto.get().setInherits(null);
            inheritsFrom.setValue(null);
            chageCb.accept(currentVto.get());

            refreshInheritedSelectionList();
        });

        Button addAction = new Button("New Action");
        addAction.addClickListener(e -> {
            ActionVto actionVto = new ActionVto();
            SceneVto sceneVto = currentVto.get();
            sceneVto.getActions().add(actionVto);
            newlyAddedActions.addComponentAsFirst(new ActionDefUi(actionVto, allScenes, q -> sceneVto.getActions().remove(q),
                    q -> chageCb.accept(currentVto.get())));
        });

        HorizontalLayout selectAbnButtonHoriz = new HorizontalLayout(inheritsFrom, removeInherits, addAction);
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

        items.removeItems(List.of(sceneVto));

        if (sceneVto.getInherits() != null)
            inheritsFrom.setValue(sceneVto.getInherits());
        else
            inheritsFrom.setValue(null);

        sceneVto.getActions().stream()
                .map(q -> new ActionDefUi(q, allScenes, true,
                        p -> sceneVto.getActions().remove(p),
                        w -> chageCb.accept(currentVto.get())))
                .forEach(ownActions::add);

//        scrapeActionsRecursive(sceneVto.getInherits()).stream()
//                .map(p -> new ActionDefUi(p, allScenes, false, o -> {
//                }))
//                .forEach(inheritedActions::add);

        return this;
    }


    public static List<ActionVto> scrapeActionsRecursive(SceneVto sceneVto) {
        return sceneVto == null ? List.of() : scrapeActionsRecursive(sceneVto, new LinkedList<>());
    }

    public static List<ActionVto> scrapeActionsRecursive(SceneVto sceneVto, List<ActionVto> actionVtos) {
        if (sceneVto.getInherits() == null)
            return sceneVto.getActions();

        actionVtos.addAll(scrapeActionsRecursive(sceneVto.getInherits(), actionVtos));

        return actionVtos;
    }

    void refreshInheritedSelectionList() {
        inheritedActions.removeAll();
        scrapeActionsRecursive(currentVto.get()).stream()
                .filter(q -> !currentVto.get().getActions().contains(q))
                .map(q -> new ActionDefUi(q, allScenes, false,
                        p -> currentVto.get().getActions().remove(p),
                        w -> chageCb.accept(currentVto.get())))
                .forEach(inheritedActions::add);
    }
}
