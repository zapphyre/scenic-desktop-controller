package org.remote.desktop.ui.select;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.*;
import org.asmus.model.GamepadDevice;
import org.remote.desktop.model.dto.SceneDto;
import org.remote.desktop.ui.select.trigger.TriggerUpdateCallback;
import org.remote.desktop.ui.select.trigger.UiSelectUpdate;

import java.util.Collection;
import java.util.function.Function;

public class UnoSelectApplication<T> extends Application {

    private final UnoBehaviourSelector<T> selector = new UnoBehaviourSelector<>();
    private final Text title = new Text();

    // Popup and invisible owner stage
    private Popup popup;
    private Stage ownerStage;

    private Runnable setCols = () -> {};
    private UiSelectUpdate.UiSelectUpdateBuilder<T> update;

    @Override
    public void start(Stage primaryStage) {
        // ---- Invisible owner stage for popup ----
        ownerStage = new Stage(StageStyle.UTILITY);
        ownerStage.setOpacity(0);
        ownerStage.setWidth(1);
        ownerStage.setHeight(1);
        ownerStage.setX(-10000);
        ownerStage.setY(-10000);
        ownerStage.setScene(new Scene(new VBox(), Color.TRANSPARENT));
        ownerStage.show();

        // ---- Title setup ----
        title.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: yellow; -fx-background-color: transparent;");
        title.setEffect(new Glow(0.8));
        title.setFill(Color.TURQUOISE);

        // ---- Main layout ----
        VBox rows = new VBox(title, selector);
        rows.setBackground(Background.EMPTY);
        rows.setStyle("-fx-padding: 10; -fx-background-color: rgba(0,0,0,0.65); -fx-background-radius: 12;");

        // ---- Scene ----
        Scene scene = new Scene(rows);
        scene.setFill(Color.TRANSPARENT);

        // ---- Popup configuration ----
        popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(false);
        popup.setHideOnEscape(true);
        popup.getContent().add(rows);

        setCols.run();

        ownerStage.requestFocus();
    }

    public TriggerUpdateCallback<T> setItems(Collection<? extends T> items,
                                             Function<? super T, String> labelGetter) {
        setCols = () -> selector.setColumns(items, labelGetter);
        selector.setOnMouseClicked(q -> ownerStage.requestFocus());

        return callback -> selector.setOnKeyPressed(event -> {
            popup.setOnShowing(q -> {
                System.out.println("popup shown====");
                ownerStage.requestFocus();
            });

            ownerStage.setOnShown(q -> {
                ownerStage.requestFocus();
//                selector.requestFocus();
                popup.requestFocus();
                System.out.println("ownerStage shown====requesting fcs");
            });

            switch (event.getCode()) {
                case DOWN -> selector.selectNext();
                case UP -> selector.selectPrevious();
                case ENTER -> callback.accept(update.element(selector.getSelected()).build());
                case ESCAPE -> close(); // optional close on ESC
            }
        });
    }

    public void close() {
        Platform.runLater(() -> {
            ownerStage.close();
            popup.hide();
        });
    }

    public void render(SceneDto lastScene, String trigger, GamepadDevice device) {
        update = UiSelectUpdate.<T>builder().sceneDto(lastScene).device(device);

        Platform.runLater(() -> {
            ownerStage.setAlwaysOnTop(true);

            if (!ownerStage.isShowing())
                ownerStage.show();

            // Compute center position
            Screen screen = Screen.getPrimary();
            double width = 400;
            double height = 300;
            double x = screen.getVisualBounds().getMinX() + (screen.getVisualBounds().getWidth() - width) / 2;
            double y = screen.getVisualBounds().getMinY() + (screen.getVisualBounds().getHeight() - height) / 2;

            popup.show(ownerStage, x, y);
            popup.getScene().setOnKeyPressed(selector.getOnKeyPressed()); // optional safeguard
            ownerStage.requestFocus();

            System.out.println("unoSelectApplication::render");
//            selector.requestFocus();
        });
    }

    public void setTitle(String name) {
        Platform.runLater(() -> title.setText(name));
    }

    public static void main(String[] args) {
        launch(args);
    }
}