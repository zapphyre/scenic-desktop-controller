package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.AxisEventFactory;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.service.JoyWorker;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.text.translator.PolarCoordsSectionTranslator;
import org.remote.desktop.text.translator.PolarSettings;
import org.remote.desktop.ui.InputWidgetBase;
import org.remote.desktop.ui.VariableGroupingInputWidgetBase;
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

    private final InputWidgetBase widget;

    private PolarCoordsSectionTranslator letterSegmentTranslator = createTranslator(new PolarSettings(210, 4));

    @PostConstruct
    void init() {
        Future<?> ui = Executors.newSingleThreadExecutor().submit(() -> Platform.startup(() -> widget.start(new Stage())));

        PolarCoordsSectionTranslator groupsTranslator = createTranslator(new PolarSettings(180, VariableGroupingInputWidgetBase.letterGroups.length));

        AxisEventFactory.leftStickStream().polarProducer(worker)
                .filter(q -> q.getRadius() > 12_000)
                .map(groupsTranslator::translate)
                .distinctUntilChanged()
                .map(widget::setGroupActive)
                .distinctUntilChanged()
                .doOnComplete(() -> ui.cancel(true))
                .subscribe(p -> letterSegmentTranslator = createTranslator(new PolarSettings(210, p)));

//        AxisEventFactory.rightStickStream().polarProducer(worker)
////                .filter(_ -> widget.isReady())
//                .map(q -> letterSegmentTranslator.translate(q))
//                .distinctUntilChanged()
//                .mapNotNull(widget::setElementActive)
//                .subscribe();

    }


}
