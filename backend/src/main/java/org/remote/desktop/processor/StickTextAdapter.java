package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.AxisEventFactory;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.model.EQualificationType;
import org.asmus.service.JoyWorker;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.model.ButtonActionDef;
import org.remote.desktop.text.translator.PolarCoordsSectionTranslator;
import org.remote.desktop.text.translator.PolarSettings;
import org.remote.desktop.ui.InputWidget;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.remote.desktop.text.translator.PolarSectionTranslatorFactory.createTranslator;

@Component
@RequiredArgsConstructor
public class StickTextAdapter {

    private final JoyWorker worker;
    private final IntrospectedEventFactory gamepadObserver;
    protected final ButtonPressMapper buttonPressMapper;

    private final InputWidget widget;

    PolarCoordsSectionTranslator letterSegmentTranslator = createTranslator(new PolarSettings(210, 4));

    @PostConstruct
    void init() {
        Future<?> ui = Executors.newSingleThreadExecutor().submit(() -> Platform.startup(() -> widget.start(new Stage())));

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
                .subscribe();

        gamepadObserver.getButtonEventStream()
                .filter(q -> q.getQualified() == EQualificationType.PUSH)
                .map(buttonPressMapper::map)
                .subscribe(this::processButtonPress);
    }

    private void processButtonPress(ButtonActionDef def) {
        System.out.println(def);
//        switch (EButtonAxisMapping.getByEnumName(def.getTrigger())) {
//            case Y: widget.clearText(); break;
//            case X: widget.close(); break;
//            case B: widget.render(); break;
//            case BUMPER_RIGHT: widget.addSelectedLetter(); break;
//        }
    }

}
