package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.remote.desktop.model.SceneVdo;

import java.util.List;
import java.util.function.Consumer;

public class SceneDialog extends Dialog {

    private final SceneVdo scene = new SceneVdo();

    public SceneDialog(List<SceneVdo> scenes, Consumer<SceneVdo> callback) {
        TextField name = new TextField("Name");
        name.addValueChangeListener(e -> scene.setName(e.getValue()));

        TextField windowName = new TextField("Window Name");
        windowName.addValueChangeListener(e -> scene.setWindowName(e.getValue()));

        ComboBox<SceneVdo> inherits = new ComboBox<>("Inherits");
        inherits.setItems(scenes);
        inherits.addValueChangeListener(e -> scene.setInherits(e.getValue()));
        inherits.setItemLabelGenerator(SceneVdo::getName);
        FormLayout formLayout = new FormLayout();

        formLayout.add(name, windowName, inherits);

        Button ok = new Button("OK", e -> callback.accept(scene));
        ok.addClickListener(e -> close());

        getFooter().add(ok);

        add(formLayout);
    }


}
