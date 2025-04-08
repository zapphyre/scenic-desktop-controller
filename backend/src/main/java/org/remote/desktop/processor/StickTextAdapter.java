package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.AxisEventFactory;
import org.asmus.service.JoyWorker;
import org.remote.desktop.text.translator.PolarCoordsSectionTranslator;
import org.remote.desktop.text.translator.PolarSettings;
import org.remote.desktop.ui.CircleWidget;
import org.remote.desktop.ui.CircleWidgetOld;
import org.remote.desktop.ui.InputWidget;
import org.remote.desktop.ui.JavaFxApplication;
import org.remote.desktop.ui.model.WidgetSettings;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.remote.desktop.text.translator.PolarSectionTranslatorFactory.createTranslator;

@Component
@RequiredArgsConstructor
public class StickTextAdapter {

    private final JoyWorker worker;

    @PostConstruct
    void init() {
//        CircleWidgetOld circleWidgetOldLeft = new CircleWidgetOld(3, Color.BURLYWOOD, 0.4, Color.ORANGE, Color.BLACK);
//        CircleWidgetOld circleWidgetRight = new CircleWidgetOld(3, Color.BURLYWOOD, .1, Color.ORANGE, Color.WHITE);
//
        InputWidget widget = new InputWidget(3, Color.BURLYWOOD, 0.4, Color.ORANGE, Color.BLACK, 6);

//        WidgetSettings settings = new WidgetSettings(3, Color.BURLYWOOD, 0.4, Color.ORANGE, Color.BLACK,
//                2, 40, 90, new String[]{}, 120);
//        CircleWidget cw;

        new Thread(() -> {
            Platform.startup(() -> {
//                Scene scene = circleWidgetOldLeft.createScene(2, 4, 40, 90, new String[]{}, 120);

                Stage primaryStage = new Stage();
//                primaryStage.setScene(scene);
//                primaryStage.show();
                widget.start(primaryStage);
//                CircleWidget cw = new CircleWidget(settings);
//                Stage stage = new Stage();
//                stage.setScene(cw);
//                stage.show();
            });
        })
//        ;
                .start();

//        circleWidgetOldLeft.setLetterGroups(letterGroups);
//        circleWidgetRight.setHighlightedSection(2);


        PolarCoordsSectionTranslator groupsTranslator = createTranslator(new PolarSettings(250, 6));


        AxisEventFactory.leftStickStream().polarProducer(worker)
                .map(groupsTranslator::translate)
                .distinctUntilChanged()
                .doOnNext(widget::groupCircleHighlight)
                .subscribe(q -> System.out.println(q));
//                .subscribe(q -> AxisEventFactory.leftStickStream().polarProducer(worker)
//                        .map(createTranslator(new PolarSettings(250, q))::translate)
//                        .distinctUntilChanged()
//                        .map(p -> new GroupPos(arraize(letterGroups[q]), p))
//                        .subscribe(p -> circleWidgetRight.updateSlicesAndLabels(p.group, p.pos))
//                );

    }

    record GroupPos(String[] group, int pos) {}
}
