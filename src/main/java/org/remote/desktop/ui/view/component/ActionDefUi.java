package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.theme.lumo.LumoIcon;
import org.asmus.model.EButtonAxisMapping;
import org.remote.desktop.model.ActionVto;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ActionDefUi extends HorizontalLayout {

    private final HorizontalLayout triggerSection = new HorizontalLayout();
    private final ActionVto active;
//    private final HorizontalLayout modifiersSection = new HorizontalLayout();
//    private final HorizontalLayout actionSection = new HorizontalLayout();

    Icon dirty = LumoIcon.ERROR.create();

    List<String> buttonNames = Arrays.stream(EButtonAxisMapping.values()).map(Enum::name).toList();
    int orighash;

    public ActionDefUi(ActionVto input, Consumer<ActionVto> remover) {
        this(input, true, remover);
    }

    public ActionDefUi(ActionVto input, boolean enabled, Consumer<ActionVto> remover) {
        this.active = input;
        dirty.setVisible(false);
        orighash = input.hashCode();

        setAlignItems(Alignment.END);

        Select<EButtonAxisMapping> trigger = new Select<>("Button Trigger", q -> input.setTrigger(q.getValue()));
        trigger.setItems(EButtonAxisMapping.values());
        trigger.setValue(input.getTrigger());
        trigger.setItemLabelGenerator(EButtonAxisMapping::name);
        trigger.addValueChangeListener(q -> computeDirty(input));
        trigger.setEnabled(enabled);

        Checkbox longPress = new Checkbox("Long Press");
        longPress.setValue(input.isLongPress());
        longPress.addValueChangeListener(q -> input.setLongPress(q.getValue()));
        longPress.setEnabled(enabled);

        MultiSelectComboBox<EButtonAxisMapping> modifiers = new MultiSelectComboBox<>("Modifiers");
        modifiers.setItems(EButtonAxisMapping.values());
        modifiers.setValue(input.getModifiers());
        modifiers.setAutoExpand(MultiSelectComboBox.AutoExpandMode.HORIZONTAL);
        modifiers.addValueChangeListener(q -> input.setModifiers(q.getValue()));
        modifiers.addValueChangeListener(q -> computeDirty(input));
        modifiers.setWidthFull();
        modifiers.setEnabled(enabled);

        setAlignItems(Alignment.BASELINE);
        VerticalLayout icoWrap = new VerticalLayout();
        icoWrap.setAlignItems(Alignment.BASELINE);
        icoWrap.add(dirty);

        VerticalLayout dlay = new VerticalLayout(dirty);
        dlay.setAlignItems(Alignment.BASELINE);
        dlay.setHeightFull();
//        triggerSection.add(new HorizontalLayout(dlay));

        XdoActionMgrUi actionMgrUi = new XdoActionMgrUi(input.getActions(), enabled);
//        actionSection.add(actionMgrUi);
//        actionSection.add(new TextField("Action_*2"));
//        KeyInputUi keyInputUi = new KeyInputUi(input);
//        actionSection.add(keyInputUi);

//        triggerSection.setAlignItems(Alignment.CENTER);

        Button rem = new Button("-", q -> remover.accept(input));
        rem.addClickListener(e -> getParent().ifPresent(q -> ((HasComponents) q).remove(this)));
        rem.setVisible(enabled);

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

    void computeDirty(ActionVto scene) {
        dirty.setVisible(orighash != scene.hashCode());
    }
}
