package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.remote.desktop.component.SceneDao;
import org.remote.desktop.model.GPadEventVto;
import org.remote.desktop.model.XdoActionVto;

import java.util.function.Consumer;

public class XdoActionMgrUi extends VerticalLayout {

    public XdoActionMgrUi(SceneDao dbToolbox, GPadEventVto gPadEventVto, boolean enabled, Consumer<XdoActionVto> chageCb) {
        setWidthFull();

        Button addButton = new Button("+");
        addButton.setAriaLabel("Add Action");
        addButton.setVisible(enabled);

        gPadEventVto.getActions().stream()
                .map(q -> new XdoActionUi(q, enabled, dbToolbox::remove, chageCb))
                .forEach(this::addComponentAsFirst);

        addButton.addClickListener(e -> {
            XdoActionVto newAction = XdoActionVto.builder()
                    .gPadEvent(gPadEventVto)
                    .build();
            XdoActionVto saved = dbToolbox.save(newAction);

            XdoActionUi xdoActionUi = new XdoActionUi(saved, enabled, dbToolbox::remove, chageCb);

            addComponentAsFirst(xdoActionUi);
        });

        setAlignItems(Alignment.CENTER);
        add(addButton);
    }

}
