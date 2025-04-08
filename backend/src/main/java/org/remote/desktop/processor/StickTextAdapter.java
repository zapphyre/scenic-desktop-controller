package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.AxisEventFactory;
import org.asmus.model.PolarCoords;
import org.asmus.service.JoyWorker;
import org.remote.desktop.text.translator.PolarCoordsSectionTranslator;
import org.remote.desktop.text.translator.PolarSettings;
import org.remote.desktop.ui.InputWidget;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static org.remote.desktop.text.translator.PolarSectionTranslatorFactory.createTranslator;

@Component
@RequiredArgsConstructor
public class StickTextAdapter {

    private final JoyWorker worker;
    PolarCoordsSectionTranslator letterSegmentTranslator = createTranslator(new PolarSettings(210, 4));

    @PostConstruct
    void init() {
        InputWidget widget = new InputWidget(2, Color.BURLYWOOD, 0.4, Color.ORANGE, Color.BLACK, 6);

        new Thread(() -> {
            Platform.startup(() -> widget.start(new Stage()));
        }).start();

        PolarCoordsSectionTranslator groupsTranslator = createTranslator(new PolarSettings(210, 7));

        AxisEventFactory.leftStickStream().polarProducer(worker)
                .map(groupsTranslator::translate)
                .distinctUntilChanged()
                .filter(_ -> widget.isReady())
                .map(widget::highlightSegmentReturnSize)
                .distinctUntilChanged()
                .subscribe(p -> letterSegmentTranslator = createTranslator(new PolarSettings(210, p)));

        AxisEventFactory.rightStickStream().polarProducer(worker)
                .filter(_ -> widget.isReady())
                .map(q -> letterSegmentTranslator.translate(q))
                .distinctUntilChanged()
                .mapNotNull(widget::pickLetterAndHighlight)
                .subscribe(System.out::println, q -> System.out.println(q.getMessage()));
    }

}
