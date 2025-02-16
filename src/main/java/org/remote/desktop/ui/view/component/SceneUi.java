package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.data.SelectListDataView;
import org.remote.desktop.component.SceneDbToolbox;
import org.remote.desktop.model.ActionVto;
import org.remote.desktop.model.SceneVto;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SceneUi extends VerticalLayout {

    private final SceneDbToolbox dbToolbox;
    private final Supplier<Collection<SceneVto>> allScenes;
//    private final Consumer<SceneVto> chageCb;

    VerticalLayout inheritedActions;
    VerticalLayout newlyAddedActions;
    VerticalLayout ownActions;
    VerticalLayout actions;

    Select<SceneVto> inheritsFrom;
    SelectListDataView<SceneVto> items;
    Button addAction;

    public SceneUi(SceneDbToolbox dbToolbox, SceneVto sceneVto, Supplier<Collection<SceneVto>> allScenes) {
        this.dbToolbox = dbToolbox;
        this.allScenes = allScenes;
//        this.chageCb = chageCb;

        inheritedActions = new VerticalLayout();
        newlyAddedActions = new VerticalLayout();
        ownActions = new VerticalLayout();
        actions = new VerticalLayout();

        inheritsFrom = new Select<>("Inherits from",
                e -> sceneVto.setInherits(e.getValue()));
        items = inheritsFrom.setItems(new LinkedList<>(allScenes.get()));
        inheritsFrom.addComponentAsFirst(new Button("[none]", e -> {
            sceneVto.setInherits(null);
            inheritsFrom.setValue(null);
//            dbToolbox.remove(sceneVto);
            refreshInheritedSelectionList(sceneVto);
        }));

        inheritsFrom.setItemLabelGenerator(SceneVto::getName);
        inheritsFrom.addValueChangeListener(e -> {
            sceneVto.setInherits(e.getValue());

            dbToolbox.update(sceneVto);
            refreshInheritedSelectionList(sceneVto);
        });

        Button removeInherits = new Button("✗", o -> {
            sceneVto.setInherits(null);
            inheritsFrom.setValue(null);

            dbToolbox.remove(sceneVto);
            refreshInheritedSelectionList(sceneVto);
        });

        addAction = new Button("New Action");


        HorizontalLayout selectAbnButtonHoriz = new HorizontalLayout(inheritsFrom, removeInherits, addAction);
        selectAbnButtonHoriz.setAlignItems(Alignment.BASELINE);

        if (sceneVto.getInherits() != null)
            inheritsFrom.setValue(sceneVto.getInherits());
        else
            inheritsFrom.setValue(null);

        sceneVto.getActions().stream()
                .map(q -> new ActionDefUi(dbToolbox, sceneVto, q, allScenes, true))
                .forEach(ownActions::add);

        addAction.addClickListener(e -> {
            ActionVto actionVto = new ActionVto();
            actionVto.setScene(sceneVto);
//            sceneVto.getActions().add(actionVto);
            newlyAddedActions.addComponentAsFirst(new ActionDefUi(dbToolbox, sceneVto, actionVto, allScenes));
        });

        actions.add(newlyAddedActions, ownActions, inheritedActions);
        add(selectAbnButtonHoriz, actions);
    }

//    Supplier<SceneVto> currentScene = SceneVto::new;

    public Component render(SceneVto sceneVto) {
//        currentScene = () -> sceneVto;
        newlyAddedActions.removeAll();
        ownActions.removeAll();
        inheritedActions.removeAll();

        items.removeItems(List.of(sceneVto));



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

    void refreshInheritedSelectionList(SceneVto sceneVto) {
        inheritedActions.removeAll();
        scrapeActionsRecursive(sceneVto).stream()
                .filter(q -> !sceneVto.getActions().contains(q))
                .map(q -> new ActionDefUi(dbToolbox, sceneVto, q, allScenes, false))
                .forEach(inheritedActions::add);
    }
}
