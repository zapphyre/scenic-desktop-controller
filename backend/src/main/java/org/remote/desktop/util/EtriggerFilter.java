package org.remote.desktop.util;

import lombok.experimental.UtilityClass;
import org.asmus.model.GamepadEvent;

import java.util.function.Predicate;

@UtilityClass
public class EtriggerFilter {

    public static Predicate<GamepadEvent> triggerUpTo(int num) {
        return q -> q.getType().ordinal() <= num;
    }

    public static Predicate<GamepadEvent> triggerByOf(int num) {
        return q -> q.getType().ordinal() >= num;
    }

    public static Predicate<GamepadEvent> triggerBetween(int fromInclusive, int toExclusive) {
        return q -> q.getType().ordinal() >= fromInclusive && q.getType().ordinal() < toExclusive;
    }

}
