package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.vto.XdoActionVto;

import java.util.Optional;
import java.util.function.Consumer;

public class XdoActionUi extends HorizontalLayout {


    public XdoActionUi(int idx, XdoActionVto xdoAction, boolean enabled, Consumer<XdoActionVto> remover, Consumer<XdoActionVto> chageCb) {
        setSizeFull();
        setAlignItems(Alignment.BASELINE);
        Button remove = new Button("-", e -> remover.accept(xdoAction));
        remove.setVisible(enabled);

        remove.addClickListener(e -> getParent()
                .ifPresent(q -> ((HasComponents)q).remove(this)));

        Select<EKeyEvt> keyEvtComboBox = new Select<>(idx + ". Press", q -> {});
        keyEvtComboBox.setItems(EKeyEvt.values());
        keyEvtComboBox.setItemLabelGenerator(EKeyEvt::name);
        keyEvtComboBox.setEnabled(enabled);
        keyEvtComboBox.setValue(Optional.ofNullable(xdoAction).map(XdoActionVto::getKeyEvt).orElse(EKeyEvt.STROKE));
        keyEvtComboBox.addValueChangeListener(q -> {
            xdoAction.setKeyEvt(q.getValue());
            chageCb.accept(xdoAction);
        });
//        keyEvtComboBox.addValueChangeListener(q -> chageCb.accept(xdoAction));

        TextField key = new TextField("Key");
        Optional.ofNullable(xdoAction).map(XdoActionVto::getKeyPress).ifPresent(key::setValue);
        key.setEnabled(enabled);
        key.addValueChangeListener(q -> {
            xdoAction.setKeyPress(q.getValue());
            chageCb.accept(xdoAction);
        });
//        key.addValueChangeListener(e -> chageCb.accept(xdoAction));

        add(keyEvtComboBox, key, remove);
    }
}
