package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.XdoActionVto;

import java.util.Optional;
import java.util.function.Consumer;

public class XdoActionUi extends HorizontalLayout {


    public XdoActionUi(XdoActionVto xdoAction, Consumer<XdoActionVto> remover, Consumer<XdoActionVto> chageCb) {
        this(xdoAction, true, remover, chageCb);
    }

    public XdoActionUi(XdoActionVto xdoAction, boolean enabled, Consumer<XdoActionVto> remover, Consumer<XdoActionVto> chageCb) {
        setSizeFull();
        setAlignItems(Alignment.BASELINE);
        Button remove = new Button("-", e -> remover.accept(xdoAction));
        remove.setVisible(enabled);

        remove.addClickListener(e -> getParent()
                .ifPresent(q -> ((HasComponents)q).remove(this)));

        Select<EKeyEvt> keyEvtComboBox = new Select<>("First Press", q -> {});
        keyEvtComboBox.setItems(EKeyEvt.values());
        keyEvtComboBox.setItemLabelGenerator(EKeyEvt::name);
        keyEvtComboBox.setValue(xdoAction.getKeyEvt());
        keyEvtComboBox.setEnabled(enabled);
        keyEvtComboBox.addValueChangeListener(q -> xdoAction.setKeyEvt(q.getValue()));
        keyEvtComboBox.addValueChangeListener(q -> chageCb.accept(xdoAction));

        TextField key = new TextField("Key");
        Optional.ofNullable(xdoAction.getKeyPress()).ifPresent(key::setValue);
        key.setEnabled(enabled);
        key.addValueChangeListener(q -> xdoAction.setKeyPress(q.getValue()));
        key.addValueChangeListener(e -> chageCb.accept(xdoAction));

        add(keyEvtComboBox, key, remove);
    }
}
