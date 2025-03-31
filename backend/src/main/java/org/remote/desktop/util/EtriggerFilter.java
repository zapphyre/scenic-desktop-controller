package org.remote.desktop.util;

import lombok.experimental.UtilityClass;
import org.asmus.model.EButtonAxisMapping;
import org.asmus.model.GamepadEvent;

import java.util.function.Predicate;

@UtilityClass
public class EtriggerFilter {

    public static Predicate<GamepadEvent> triggerUpTo(EButtonAxisMapping incl) {
        return q -> q.getType().ordinal() <= incl.ordinal();
    }

    public static Predicate<GamepadEvent> triggerByOf(EButtonAxisMapping incl) {
        return q -> q.getType().ordinal() >= incl.ordinal();
    }

    public static Predicate<GamepadEvent> triggerBetween(EButtonAxisMapping incl, EButtonAxisMapping excl) {
        return q -> q.getType().ordinal() >= incl.ordinal() &&
                q.getType().ordinal() < excl.ordinal();
    }

}
