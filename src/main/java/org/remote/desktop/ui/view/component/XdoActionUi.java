package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.XdoActionVdo;

import java.util.Optional;
import java.util.function.Consumer;

public class XdoActionUi extends HorizontalLayout {

    private final XdoActionVdo xdoAction;

    public XdoActionUi(XdoActionVdo xdoAction, Consumer<XdoActionVdo> remover) {
        this.xdoAction = xdoAction;

        setAlignItems(Alignment.BASELINE);
        Button remove = new Button("-", e -> remover.accept(xdoAction));

//        remove.addClickListener(e -> parent.remove(this));
        remove.addClickListener(e -> getParent()
                .ifPresent(q -> ((HasComponents)q).remove(this)));

        ComboBox<EKeyEvt> keyEvtComboBox = new ComboBox<>("First Press");
        keyEvtComboBox.setItems(EKeyEvt.values());
        keyEvtComboBox.setItemLabelGenerator(EKeyEvt::name);
        keyEvtComboBox.setValue(xdoAction.getKeyEvt());
        keyEvtComboBox.addValueChangeListener(q ->
                xdoAction.setKeyEvt(q.getValue())
        );

        TextField key = new TextField("key");
        key.addValueChangeListener(q -> xdoAction.setKeyPress(q.getValue()));
        Optional.ofNullable(xdoAction.getKeyPress())
                        .ifPresent(key::setValue);

        add(keyEvtComboBox, key, remove);
    }
}
