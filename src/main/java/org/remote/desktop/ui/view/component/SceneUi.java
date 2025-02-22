package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.vto.GPadEventVto;
import org.remote.desktop.model.vto.SceneVto;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class SceneUi extends VerticalLayout {

    private final SceneDao dbToolbox;
    private final Supplier<Collection<SceneVto>> allScenes;
//    private final Consumer<SceneVto> chageCb;

    VerticalLayout inheritedActions;
    VerticalLayout newlyAddedActions;
    VerticalLayout ownActions;
    VerticalLayout actions;

    Select<SceneVto> inheritsFrom;
    Button addAction;

    public SceneUi(SceneDao dbToolbox, SceneVto sceneVto, Supplier<Collection<SceneVto>> allScenes) {
        this.dbToolbox = dbToolbox;
        this.allScenes = allScenes;
//        this.chageCb = chageCb;

        inheritedActions = new VerticalLayout();
        newlyAddedActions = new VerticalLayout();
        ownActions = new VerticalLayout();
        actions = new VerticalLayout();

        inheritsFrom = new Select<>("Inherits from",
                e -> sceneVto.setInherits(e.getValue()));
        inheritsFrom.setItems(scenesWithout(allScenes.get(), sceneVto));
        inheritsFrom.addComponentAsFirst(new FullWidthButton("[none]", e -> {
            inheritsFrom.setValue(null);
        }));

        inheritsFrom.setItemLabelGenerator(SceneVto::getName);
        inheritsFrom.addValueChangeListener(e -> {
            sceneVto.setInherits(e.getValue());

            dbToolbox.update(sceneVto);
            refreshInheritedSelectionList(sceneVto);
        });

        addAction = new Button("New Action");

        HorizontalLayout selectAbnButtonHoriz = new HorizontalLayout(inheritsFrom, addAction);
        selectAbnButtonHoriz.setAlignItems(Alignment.BASELINE);

        if (sceneVto.getInherits() != null)
            inheritsFrom.setValue(sceneVto.getInherits());
        else
            inheritsFrom.setValue(null);

        sceneVto.getGPadEvents().stream()
                .map(q -> new ActionDefUi(dbToolbox, sceneVto, q, allScenes, true))
                .forEach(ownActions::add);

        addAction.addClickListener(e -> {
            GPadEventVto GPadEventVto = new GPadEventVto();
            GPadEventVto.setScene(sceneVto);
            sceneVto.getGPadEvents().add(GPadEventVto = dbToolbox.save(GPadEventVto));
            newlyAddedActions.addComponentAsFirst(new ActionDefUi(dbToolbox, sceneVto, GPadEventVto, allScenes));
        });

        actions.add(newlyAddedActions, ownActions, inheritedActions);
        add(selectAbnButtonHoriz, actions);
    }

    public static List<GPadEventVto> scrapeActionsRecursive(SceneVto sceneVto) {
        return sceneVto == null ? List.of() : scrapeActionsRecursive(sceneVto, new LinkedList<>());
    }

    public static List<GPadEventVto> scrapeActionsRecursive(SceneVto sceneVto, List<GPadEventVto> GPadEventVtos) {
        if (sceneVto.getInherits() == null)
            return sceneVto.getGPadEvents();

        GPadEventVtos.addAll(scrapeActionsRecursive(sceneVto.getInherits(), GPadEventVtos));

        return GPadEventVtos;
    }

    static List<SceneVto> scenesWithout(Collection<SceneVto> sceneVtos, SceneVto sceneVto) {
        return sceneVtos.stream()
                .filter(q -> !q.getName().equals(sceneVto.getName()))
                .toList();
    }

    void refreshInheritedSelectionList(SceneVto sceneVto) {
        inheritedActions.removeAll();
        scrapeActionsRecursive(sceneVto).stream()
                .filter(q -> !sceneVto.getGPadEvents().contains(q))
                .map(q -> new ActionDefUi(dbToolbox, sceneVto, q, allScenes, false))
                .forEach(inheritedActions::add);
    }
}
