package org.remote.desktop.ui.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.EAxisEvent;
import org.remote.desktop.model.vto.GPadEventVto;
import org.remote.desktop.model.vto.SceneVto;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
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

        Select<EAxisEvent> leftAxis = bindAxis(sceneVto, SceneVto::getLeftAxisEvent, sceneVto::setLeftAxisEvent, dbToolbox::update, "Left Axis");
        Select<EAxisEvent> rightAxis = bindAxis(sceneVto, SceneVto::getRightAxisEvent, sceneVto::setRightAxisEvent, dbToolbox::update, "Right Axis");

        addAction = new Button("New Action");

        HorizontalLayout selectAbnButtonHoriz = new HorizontalLayout(inheritsFrom, leftAxis, rightAxis);
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
        add(selectAbnButtonHoriz, addAction, actions);
    }

    public static List<GPadEventVto> scrapeActionsRecursive(SceneVto sceneVto) {
        return sceneVto == null ? List.of() : scrapeActionsRecursive(sceneVto, new LinkedList<>());
    }

    public static List<GPadEventVto> scrapeActionsRecursive(SceneVto sceneVto, List<GPadEventVto> GPadEventVtos) {
        if (sceneVto != null)
            scrapeActionsRecursive(sceneVto.getInherits(), GPadEventVtos);

        Optional.ofNullable(sceneVto)
                .map(SceneVto::getGPadEvents)
                .ifPresent(GPadEventVtos::addAll);

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

    Select<EAxisEvent> bindAxis(SceneVto scene, Function<SceneVto, EAxisEvent> getter, Consumer<EAxisEvent> setter, Consumer<SceneVto> updater, String title) {
        Select<EAxisEvent> axisSelect = new Select<>(title, q -> {});
        axisSelect.setItems(EAxisEvent.values());
        axisSelect.setItemLabelGenerator(EAxisEvent::name);
        axisSelect.setValue(getter.apply(scene));
        axisSelect.addValueChangeListener(e -> {
            setter.accept(e.getValue());
            updater.accept(scene);
        });

        return axisSelect;
    }
}
