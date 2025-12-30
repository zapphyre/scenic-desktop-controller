package org.remote.desktop.ui.select;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.model.EAnalogControl;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.ui.select.axis.AxisUpdate;
import org.remote.desktop.ui.select.axis.SelectedCallback;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class DuoSelectApplication<L, R> extends Application {
    private final DuoBehaviourSelector<L, R> selector = new DuoBehaviourSelector<>();
    private Stage primaryStage;

    private Runnable setCols = () -> {
    };
    private AxisUpdate.AxisUpdateBuilder<L, R> update;

    public SelectedCallback<L, R> setItems(List<? extends L> leftItems, List<? extends R> rightItems,
                                           Function<? super L, String> leftLabelGetter, Function<? super R, String> rightLabelGetter) {

        setCols = () -> selector.setColumns(
                leftItems, rightItems, leftLabelGetter, rightLabelGetter
        );

        return callback ->
                selector.setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case DOWN:
                            selector.selectNext();
                            break;
                        case UP:
                            selector.selectPrevious();
                            break;
                        case LEFT:
                            selector.switchToLeft();
                            break;
                        case RIGHT:
                            selector.switchToRight();
                            break;
                        case ENTER:
                            callback.accept(update.left(selector.getSelected().getKey()).right(selector.getSelected().getValue()).build());
                            break;
                    }
                });
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        selector.initOwnerStage();
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

    public void render(SceneDto lastScene, EAnalogControl trigger, L l, R r) {
        update = AxisUpdate.<L, R>builder().trigger(trigger).sceneDto(lastScene);
        selector.select(l, r);

        Platform.runLater(() -> {
            this.primaryStage.setAlwaysOnTop(true);
            this.primaryStage.show();
            this.primaryStage.requestFocus();
            this.primaryStage.toFront();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}