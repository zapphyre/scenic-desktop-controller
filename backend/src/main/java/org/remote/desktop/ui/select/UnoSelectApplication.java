package org.remote.desktop.ui.select;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.ui.select.trigger.TriggerUpdateCallback;
import org.remote.desktop.ui.select.trigger.UiSelectUpdate;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class UnoSelectApplication<T> extends Application {

    private final UnoBehaviourSelector<T> selector = new UnoBehaviourSelector<>();
    private Stage primaryStage;
    private Runnable setCols = () -> {
    };

    private UiSelectUpdate.UiSelectUpdateBuilder<T> update;

    public TriggerUpdateCallback<T> setItems(Collection<? extends T> items,
                                             Function<? super T, String> labelGetter) {

        setCols = () -> selector.setColumns(items, labelGetter);

        return callback ->
                selector.setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case DOWN:
                            selector.selectNext();
                            break;
                        case UP:
                            selector.selectPrevious();
                            break;
                        case ENTER:
                            callback.accept(update.analogControl(selector.getSelected()).build());
                            break;
                    }
                });
    }


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        Scene scene = new Scene(selector, 400, 300);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Trigger Selector");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.hide();
        });

        setCols.run();
        selector.requestFocus();
    }

    public void close() {
        Platform.runLater(() -> primaryStage.hide());
    }

    public void render(SceneDto lastScene, String trigger) {
        update = UiSelectUpdate.<T>builder().trigger(trigger).sceneDto(lastScene);

        Platform.runLater(() -> {
            this.primaryStage.show();
            this.primaryStage.toFront();
            this.primaryStage.requestFocus();
        });
    }
}
