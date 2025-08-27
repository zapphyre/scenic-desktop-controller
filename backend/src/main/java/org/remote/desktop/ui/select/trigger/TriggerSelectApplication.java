package org.remote.desktop.ui.select.trigger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.remote.desktop.model.EAxisEaser;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.ui.select.UnoBehaviourSelector;

import java.util.List;
import java.util.function.Function;

public class TriggerSelectApplication extends Application {

    private final UnoBehaviourSelector<EAxisEaser> selector = new UnoBehaviourSelector<>();
    private Stage primaryStage;
    private Runnable setCols = () -> {
    };

    private TriggerUpdate.TriggerUpdateBuilder update;

    public TriggerUpdateCallback setItems(List<? extends EAxisEaser> items,
                                          Function<? super EAxisEaser, String> labelGetter) {

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
                            callback.accept(update.easer(selector.getSelected()).build());
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
        update = TriggerUpdate.builder().trigger(trigger).sceneDto(lastScene);

        Platform.runLater(() -> {
            this.primaryStage.requestFocus();
            this.primaryStage.show();
        });
    }
}
