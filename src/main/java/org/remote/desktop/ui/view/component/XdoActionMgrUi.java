package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.remote.desktop.component.SceneDao;
import org.remote.desktop.model.EKeyEvt;
import org.remote.desktop.model.GPadEventVto;
import org.remote.desktop.model.XdoActionVto;

import java.util.function.Consumer;

public class XdoActionMgrUi extends VerticalLayout {

    public XdoActionMgrUi(SceneDao dbToolbox, GPadEventVto gPadEventVto, boolean enabled, Consumer<XdoActionVto> changeCb) {
        setWidthFull();

        Button addButton = new Button("+");
        addButton.setAriaLabel("Add Action");
        addButton.setVisible(enabled);

        gPadEventVto.getActions().stream()
                .map(q -> new XdoActionUi(q, enabled, dbToolbox::remove, changeCb))
                .forEach(this::addComponentAsFirst);

        addButton.addClickListener(e -> {
            XdoActionVto newAction = XdoActionVto.builder()
                    .gPadEvent(gPadEventVto)
                    .keyEvt(EKeyEvt.STROKE)
                    .build();

            XdoActionVto saved = dbToolbox.save(newAction);

            //has to he here, b/c in entity hibernate hook does this, but it still needs to be added here
            gPadEventVto.getActions().add(saved);

            XdoActionUi xdoActionUi = new XdoActionUi(saved, enabled, dbToolbox::remove, changeCb);

            add(xdoActionUi);
        });

        setAlignItems(Alignment.CENTER);
        add(addButton);
    }

}
