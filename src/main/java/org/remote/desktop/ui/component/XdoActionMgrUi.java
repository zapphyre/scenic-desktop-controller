package org.remote.desktop.ui.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.remote.desktop.db.dao.SceneDao;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.vto.GPadEventVto;
import org.remote.desktop.model.vto.XdoActionVto;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class XdoActionMgrUi extends VerticalLayout {

    public XdoActionMgrUi(SceneDao dbToolbox, GPadEventVto gPadEventVto, boolean enabled, Consumer<XdoActionVto> changeCb) {
        setWidthFull();

        Button addButton = new Button("+");
        addButton.setAriaLabel("Add Action");
        addButton.setVisible(enabled);

        AtomicInteger i = new AtomicInteger();
        gPadEventVto.getActions().stream()
                .map(q -> new XdoActionUi(i.incrementAndGet(), q, enabled, dbToolbox::remove, changeCb))
                .forEach(this::add);

        addButton.addClickListener(e -> {
            XdoActionVto newAction = XdoActionVto.builder()
                    .gPadEvent(gPadEventVto)
                    .keyEvt(EKeyEvt.STROKE)
                    .build();

            XdoActionVto saved = dbToolbox.save(newAction);

            //has to he here, b/c in entity hibernate hook does this, but it still needs to be added here
            gPadEventVto.getActions().add(saved);

            XdoActionUi xdoActionUi = new XdoActionUi(gPadEventVto.getActions().size(), saved, enabled, q -> {
                gPadEventVto.getActions().remove(q);
                dbToolbox.remove(q);
            }, changeCb);

            add(xdoActionUi);
        });

        setAlignItems(Alignment.CENTER);
        add(addButton);
    }

}
