package org.remote.desktop.ui.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.remote.desktop.model.SceneVto;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SceneDialog extends Dialog {

    public SceneDialog(List<SceneVto> scenes, Consumer<SceneVto> callback) {
        this(null, scenes, callback, q -> {
        });
    }

    public SceneDialog(SceneVto in, List<SceneVto> scenes, Consumer<SceneVto> okCallback, Consumer<SceneVto> removeCallback) {
        SceneVto scene = in == null ? SceneVto.builder()
                .windowName("")
                .name("")
                .build() : in;

        TextField name = new TextField("Name");
        name.setValue(scene.getName());
        name.addValueChangeListener(e -> scene.setName(e.getValue()));

        TextField windowName = new TextField("Window Name");
        windowName.setValue(scene.getWindowName());
        windowName.addValueChangeListener(e -> scene.setWindowName(e.getValue()));

        ComboBox<SceneVto> inherits = new ComboBox<>("Inherits");
        inherits.setItems(scenes);
        inherits.setValue(scene);
        inherits.addValueChangeListener(e -> scene.setInherits(e.getValue()));
        inherits.setItemLabelGenerator(SceneVto::getName);
        FormLayout formLayout = new FormLayout();

        formLayout.add(name, windowName, inherits);


        if (Objects.nonNull(in))
            getFooter().add(new Button("Remove", e -> {
                removeCallback.accept(scene);
                close();
            }));

        getFooter().add(new Button("Save", e -> {
            okCallback.accept(scene);
            close();
        }));

        add(formLayout);
    }


}
