package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.remote.desktop.model.XdoActionVto;

import java.util.List;
import java.util.function.Consumer;

public class XdoActionMgrUi extends VerticalLayout {

    public XdoActionMgrUi(List<XdoActionVto> xdoActions, Consumer<XdoActionVto> chageCb) {
        this(xdoActions, true, chageCb);
    }

    public XdoActionMgrUi(List<XdoActionVto> xdoActions, boolean enabled, Consumer<XdoActionVto> chageCb) {

        setAlignItems(Alignment.CENTER);
        Button addButton = new Button("+");
        addButton.setAriaLabel("Add Action");
        addButton.setVisible(enabled);

        VerticalLayout actionsWrapper = new VerticalLayout();

        addButton.addClickListener(e -> {
            XdoActionVto newAction = new XdoActionVto();

//            if (xdoActions.isEmpty())
//                add(actionsWrapper);

            xdoActions.add(newAction);
            XdoActionUi xdoActionUi = new XdoActionUi(newAction, enabled, xdoActions::remove, chageCb);
            actionsWrapper.add(xdoActionUi);
        });

        xdoActions.stream()
                .map(q -> new XdoActionUi(q, enabled, xdoActions::remove, chageCb))
                .forEach(actionsWrapper::add);

//        if (!xdoActions.isEmpty())
//            add(actionsWrapper);

        add(actionsWrapper, addButton);
    }

}
