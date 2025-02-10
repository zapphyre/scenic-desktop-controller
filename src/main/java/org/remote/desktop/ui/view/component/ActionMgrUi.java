package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.remote.desktop.model.XdoActionVdo;

import java.util.List;

public class ActionMgrUi extends VerticalLayout {

    private final List<XdoActionVdo> xdoActions;

    public ActionMgrUi(List<XdoActionVdo> xdoActions) {
        this.xdoActions = xdoActions;

        setAlignItems(Alignment.CENTER);
        Button button = new Button("+");
        button.setAriaLabel("Add Action");

        button.addClickListener(e -> {
            XdoActionVdo newAction = new XdoActionVdo();
            xdoActions.add(newAction);
            XdoActionUi xdoActionUi = new XdoActionUi(newAction, xdoActions::remove);
            add(xdoActionUi);
        });

        add(button);

        xdoActions.stream()
                .map(q -> new XdoActionUi(q, xdoActions::remove))
                .forEach(this::add);
    }
}
