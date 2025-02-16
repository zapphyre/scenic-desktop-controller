package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.component.SceneDbToolbox;
import org.remote.desktop.model.ActionVto;
import org.remote.desktop.model.SceneVto;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ActionDefUi extends HorizontalLayout {

    private final HorizontalLayout triggerSection = new HorizontalLayout();

    List<String> buttonNames = Arrays.stream(EButtonAxisMapping.values()).map(Enum::name).toList();

    public ActionDefUi(SceneDbToolbox dbToolbox, SceneVto parent, ActionVto input, Supplier<Collection<SceneVto>> allScenes) {
        this(dbToolbox, parent, input, allScenes, true);
    }

    public ActionDefUi(SceneDbToolbox dbToolbox, SceneVto parent, ActionVto input, Supplier<Collection<SceneVto>> allScenes, boolean enabled) {
        setAlignItems(Alignment.END);

        Select<EButtonAxisMapping> trigger = new Select<>("Button Trigger", q -> input.setTrigger(q.getValue()));
        trigger.setItems(EButtonAxisMapping.values());
        trigger.setValue(input.getTrigger());
        trigger.setItemLabelGenerator(EButtonAxisMapping::name);
        trigger.addValueChangeListener(q -> dbToolbox.update(input));
        trigger.setEnabled(enabled);

        Checkbox longPress = new Checkbox("Long press");
        longPress.setValue(input.isLongPress());
        longPress.addValueChangeListener(q -> input.setLongPress(q.getValue()));
        longPress.addValueChangeListener(q -> dbToolbox.update(input));
        longPress.setEnabled(enabled);

        MultiSelectComboBox<EButtonAxisMapping> modifiers = new MultiSelectComboBox<>("Modifiers");
        modifiers.setItems(EButtonAxisMapping.values());
        modifiers.setValue(input.getModifiers());
        modifiers.setAutoExpand(MultiSelectComboBox.AutoExpandMode.HORIZONTAL);
        modifiers.addValueChangeListener(q -> input.getModifiers().addAll(q.getValue()));
        modifiers.addValueChangeListener(q -> dbToolbox.update(input));
        modifiers.setWidthFull();
        modifiers.setEnabled(enabled);

        Select<SceneVto> nextSceneSelect = new Select<>("Next Scene", q -> input.setNextScene(q.getValue()));
        nextSceneSelect.setItems(allScenes.get());
        nextSceneSelect.setValue(input.getNextScene());
        nextSceneSelect.setItemLabelGenerator(SceneVto::getName);
        nextSceneSelect.setEnabled(enabled);
        nextSceneSelect.addComponentAsFirst(new FullWidthButton("[none]", e -> {
            nextSceneSelect.setValue(null);
        }));
        nextSceneSelect.addValueChangeListener(q -> dbToolbox.update(input));

        XdoActionMgrUi actionMgrUi = new XdoActionMgrUi(dbToolbox, input, input.getActions(), enabled, dbToolbox::update);

        Button rem = new Button("-", q -> {
            dbToolbox.remove(input);
            parent.getActions().remove(input);
        });
        rem.addClickListener(e -> getParent().ifPresent(q -> ((HasComponents) q).remove(this)));
        rem.setVisible(enabled);

        triggerSection.add(rem, trigger, modifiers, new VerticalLayout(longPress));
        triggerSection.setAlignItems(Alignment.BASELINE);
        triggerSection.setWidthFull();

        VerticalLayout actionRowWrapper = new VerticalLayout();
        actionRowWrapper.add(triggerSection, nextSceneSelect);
        actionRowWrapper.setPadding(false);
        actionRowWrapper.setAlignItems(Alignment.BASELINE);
//        Button button = new Button();
//        button.setVisible(false);
//        VerticalLayout actionsWplaceholder = new VerticalLayout(button, actionMgrUi);
        HorizontalLayout horizontalLayout = new HorizontalLayout(actionRowWrapper, actionMgrUi);
        horizontalLayout.setAlignItems(Alignment.BASELINE);

        add(horizontalLayout);
    }
}
