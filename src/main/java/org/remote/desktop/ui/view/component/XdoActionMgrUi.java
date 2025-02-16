package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.remote.desktop.component.SceneDbToolbox;
import org.remote.desktop.model.ActionVto;
import org.remote.desktop.model.XdoActionVto;

import java.util.List;
import java.util.function.Consumer;

public class XdoActionMgrUi extends VerticalLayout {

    public XdoActionMgrUi(SceneDbToolbox dbToolbox, ActionVto parent, List<XdoActionVto> xdoActions, Consumer<XdoActionVto> chageCb) {
        this(dbToolbox, parent, xdoActions, true, chageCb);
    }

    public XdoActionMgrUi(SceneDbToolbox dbToolbox, ActionVto parent, List<XdoActionVto> xdoActions, boolean enabled, Consumer<XdoActionVto> chageCb) {

        setAlignItems(Alignment.CENTER);
        Button addButton = new Button("+");
        addButton.setAriaLabel("Add Action");
        addButton.setVisible(enabled);

        VerticalLayout actionsWrapper = new VerticalLayout();

        xdoActions.stream()
                .map(q -> new XdoActionUi(q, enabled, o -> {
                    xdoActions.remove(o);
                    dbToolbox.remove(o);
                }, qr -> {
                    System.out.println("invoking xdoActUi update callback for some reason");
                    chageCb.accept(qr);
                }))
                .forEach(components -> {
                    System.out.println("before adding existing");
                    actionsWrapper.add(components);
                });

        addButton.addClickListener(e -> {
            XdoActionVto newAction = XdoActionVto.builder()
                    .action(parent)
                    .build();
            parent.getActions().add(newAction);

            System.out.println("addButton before save");
            XdoActionVto saved = dbToolbox.save(newAction);

            xdoActions.add(saved);
            XdoActionUi xdoActionUi = new XdoActionUi(saved, enabled, o -> {
                xdoActions.remove(o);
                dbToolbox.remove(o);
            }, chageCb);

            System.out.println("before adding");
            actionsWrapper.add(xdoActionUi);
            System.out.println("after adding");
        });

        add(actionsWrapper, addButton);
    }

}
