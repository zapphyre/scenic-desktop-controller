package org.remote.desktop.mode.modul;

import lombok.RequiredArgsConstructor;
import org.desktop.remote.mode.GpadOsActionModule;
import org.remote.desktop.model.EAnalogControl;
import org.remote.desktop.model.event.GpadCommandEvent;
import org.remote.desktop.model.event.select.AnalogControllerSelectEvent;
import org.remote.desktop.model.event.select.UiAnalogAdjustEvent;
import org.remote.desktop.pojo.KeyPart;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class AnalogAdjustModule implements GpadOsActionModule {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public String getName() {
        return "ADJUST_STICK_TRIGGER";
    }

    @Override
    public List<String> getNouns() {
        return List.of();
    }

    @Override
    public List<String> getVerbs() {
        return Stream.of(EAnalogControl.values()).map(Enum::name).toList();
    }

    @Override
    public boolean isScenic() {
        return false;
    }

    @Override
    public boolean handleEvent(String verb, List<String> list) {
        GpadCommandEvent repackedEvt = new GpadCommandEvent(
                KeyPart.builder().keyEvt(verb).keyStrokes(list).build(), this
        );

        return switch (verb) {
            case "MODE_SELECT" -> {
                eventPublisher.publishEvent(new UiAnalogAdjustEvent(this, repackedEvt));

                yield  true;
            }
            case "RIGHT_TRIGGER", "RIGHT_STICK", "LEFT_STICK", "LEFT_TRIGGER" -> {
                eventPublisher.publishEvent(new AnalogControllerSelectEvent(this, EAnalogControl.valueOf(verb), null));

                yield true;
            }
            default -> false;
        };

    }

    @Override
    public boolean activate() {
        GpadCommandEvent repackedEvt = new GpadCommandEvent(
                KeyPart.builder().build(), this
        );

        eventPublisher.publishEvent(new UiAnalogAdjustEvent(this, repackedEvt));

        return GpadOsActionModule.super.activate();
    }
}
