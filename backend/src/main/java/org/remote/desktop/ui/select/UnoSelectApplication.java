package org.remote.desktop.ui.select;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.asmus.model.GamepadDevice;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.ui.select.trigger.TriggerUpdateCallback;
import org.remote.desktop.ui.select.trigger.UiSelectUpdate;

import java.util.Collection;
import java.util.function.Function;

public class UnoSelectApplication<T> extends Application {

    private final UnoBehaviourSelector<T> selector = new UnoBehaviourSelector<>();
    private final Text title = new Text();
    private Stage primaryStage;
    private Runnable setCols = () -> {
    };

    private UiSelectUpdate.UiSelectUpdateBuilder<T> update;

    public TriggerUpdateCallback<T> setItems(Collection<? extends T> items,
                                             Function<? super T, String> labelGetter) {

        setCols = () -> selector.setColumns(items, labelGetter);
        selector.requestFocus();
        selector.toFront();

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
                            callback.accept(update.element(selector.getSelected()).build());
                            break;
                    }
                });
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        title.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: yellow; -fx-background-color: transparent;");
        title.setEffect(new Glow(0.8));
        title.setFill(Color.TURQUOISE);

        VBox rows = new VBox(title, selector);
        rows.setBackground(Background.EMPTY);

        Scene scene = new Scene(rows, 400, 300);
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

    public void render(SceneDto lastScene, String trigger, GamepadDevice device) {
        update = UiSelectUpdate.<T>builder().sceneDto(lastScene).device(device);

        Platform.runLater(() -> {
            this.primaryStage.setAlwaysOnTop(true);
            this.primaryStage.show();
            this.primaryStage.toFront();
            this.primaryStage.requestFocus();
        });
    }

    public void setTitle(String name) {
        title.setText(name);
    }
}
