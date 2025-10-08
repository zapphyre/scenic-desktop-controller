package org.remote.desktop.processor;

import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.asmus.builder.AxisEventProcessorFactory;
import org.asmus.service.JoyWorker;
import org.remote.desktop.mapper.ButtonPressMapper;
import org.remote.desktop.text.translator.PolarCoordsSectionTranslator;
import org.remote.desktop.text.translator.PolarSettings;
import org.remote.desktop.ui.InputWidgetBase;
import org.remote.desktop.ui.VariableGroupingInputWidgetBase;
import org.remote.desktop.ui.scene.SceneReporter;
import org.remote.desktop.ui.select.DuoSelectApplication;
import org.remote.desktop.ui.select.UnoSelectApplication;
import org.remote.desktop.ui.select.axis.AxisUiSelector;
import org.remote.desktop.ui.select.mode.ModeSelector;
import org.remote.desktop.ui.select.trigger.TriggerUiSelector;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.remote.desktop.text.translator.PolarSectionTranslatorFactory.createTranslator;

@Component
@RequiredArgsConstructor
public class StickTextAdapter {

    private final JoyWorker worker;
    private final AxisEventProcessorFactory axisProcessors;
    protected final ButtonPressMapper buttonPressMapper;

    private final InputWidgetBase widget;
    private final AxisUiSelector duoSelectApplication;
    private final TriggerUiSelector unoSelectApplication;
    private final UnoSelectApplication<?> singleSelector;
    private final ModeSelector modeSelector;
    private final SceneReporter sceneReporter;

    private PolarCoordsSectionTranslator letterSegmentTranslator = createTranslator(new PolarSettings(210, 4));

    @PostConstruct
    void init() {
        Future<?> ui = Executors.newSingleThreadExecutor().submit(() -> {
            Platform.startup(() -> {
                widget.start(new Stage());
                singleSelector.start(new Stage());
                duoSelectApplication.start(new Stage());
                unoSelectApplication.start(new Stage());
                modeSelector.getApplication().start(new Stage());
                sceneReporter.start(new Stage());
            });
        });

        PolarCoordsSectionTranslator groupsTranslator = createTranslator(new PolarSettings(180, VariableGroupingInputWidgetBase.letterGroups.length));

        axisProcessors.leftPolarFlux().filter(q -> q.getRadius() > 12_000).map(groupsTranslator::translate).distinctUntilChanged().map(widget::setGroupActive).distinctUntilChanged()
                .doOnComplete(() -> ui.cancel(true))
                .subscribe();
//                .subscribe(p -> letterSegmentTranslator = createTranslator(new PolarSettings(210, p)));

//        AxisEventFactory.rightStickStream().polarProducer(worker)
////                .filter(_ -> widget.isReady())
//                .map(q -> letterSegmentTranslator.translate(q))
//                .distinctUntilChanged()
//                .mapNotNull(widget::setElementActive)
//                .subscribe();

    }
}
