package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoIcon;
import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.model.ActionVdo;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ActionDefUi extends HorizontalLayout {

    private final HorizontalLayout triggerSection = new HorizontalLayout();
    private final ActionVdo active;
//    private final HorizontalLayout modifiersSection = new HorizontalLayout();
//    private final HorizontalLayout actionSection = new HorizontalLayout();

    Icon dirty = LumoIcon.ERROR.create();

    List<String> buttonNames = Arrays.stream(EButtonAxisMapping.values()).map(Enum::name).toList();
    int orighash;

    public ActionDefUi(ActionVdo input, Consumer<ActionVdo> remover) {
        this.active = input;
        dirty.setVisible(false);
        orighash = input.hashCode();

        setAlignItems(Alignment.END);

        ComboBox<EButtonAxisMapping> trigger = new ComboBox<>("Button Trigger");
        trigger.setItems(EButtonAxisMapping.values());
        trigger.setValue(input.getTrigger());
        trigger.setItemLabelGenerator(EButtonAxisMapping::name);
        trigger.addValueChangeListener(q -> input.setTrigger(q.getValue()));
        trigger.addValueChangeListener(q -> computeDirty(input));

        Checkbox longPress = new Checkbox("Long Press");
        longPress.setValue(input.isLongPress());
        longPress.addValueChangeListener(q -> input.setLongPress(q.getValue()));

        MultiSelectComboBox<EButtonAxisMapping> modifiers = new MultiSelectComboBox<>("Modifiers");
        modifiers.setItems(EButtonAxisMapping.values());
        modifiers.setValue(input.getModifiers());
        modifiers.setAutoExpand(MultiSelectComboBox.AutoExpandMode.HORIZONTAL);
        modifiers.addValueChangeListener(q -> input.setModifiers(q.getValue()));
        modifiers.addValueChangeListener(q -> computeDirty(input));
        modifiers.setWidthFull();

        setAlignItems(Alignment.BASELINE);
        VerticalLayout icoWrap = new VerticalLayout();
        icoWrap.setAlignItems(Alignment.BASELINE);
        icoWrap.add(dirty);

        VerticalLayout dlay = new VerticalLayout(dirty);
        dlay.setAlignItems(Alignment.BASELINE);
        dlay.setHeightFull();
//        triggerSection.add(new HorizontalLayout(dlay));

        ActionMgrUi actionMgrUi = new ActionMgrUi(input.getActions());
//        actionSection.add(actionMgrUi);
//        actionSection.add(new TextField("Action_*2"));
//        KeyInputUi keyInputUi = new KeyInputUi(input);
//        actionSection.add(keyInputUi);

//        triggerSection.setAlignItems(Alignment.CENTER);

        Button rem = new Button("-", q -> remover.accept(input));
        rem.addClickListener(e -> getParent().ifPresent(q -> ((HasComponents) q).remove(this)));

        triggerSection.add(rem, trigger, modifiers, longPress);
        triggerSection.setAlignItems(Alignment.BASELINE);
        triggerSection.setWidthFull();

        Button button = new Button();
        button.setVisible(false);
        VerticalLayout actionsWplaceholder = new VerticalLayout(button, actionMgrUi);
        HorizontalLayout horizontalLayout = new HorizontalLayout(triggerSection, actionsWplaceholder);
        horizontalLayout.setAlignItems(Alignment.START);

        add(horizontalLayout);
    }

    void computeDirty(ActionVdo scene) {
        dirty.setVisible(orighash != scene.hashCode());
    }
}
