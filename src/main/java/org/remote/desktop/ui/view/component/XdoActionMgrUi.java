package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.remote.desktop.model.XdoActionVto;

import java.util.List;

public class XdoActionMgrUi extends VerticalLayout {

    private final List<XdoActionVto> xdoActions;

    public XdoActionMgrUi(List<XdoActionVto> xdoActions) {
        this(xdoActions, true);
    }

    public XdoActionMgrUi(List<XdoActionVto> xdoActions, boolean enabled) {
        this.xdoActions = xdoActions;

        setAlignItems(Alignment.CENTER);
        Button button = new Button("+");
        button.setAriaLabel("Add Action");
        button.setVisible(enabled);

        button.addClickListener(e -> {
            XdoActionVto newAction = new XdoActionVto();
            xdoActions.add(newAction);
            XdoActionUi xdoActionUi = new XdoActionUi(newAction, enabled, xdoActions::remove);
            add(xdoActionUi);
        });

        add(button);

        xdoActions.stream()
                .map(q -> new XdoActionUi(q, enabled, xdoActions::remove))
                .forEach(this::add);
    }

}
