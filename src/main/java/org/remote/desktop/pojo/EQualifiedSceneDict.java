package org.remote.desktop.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.behaviour.ActuationBehaviour;
import org.remote.desktop.model.Behavioral;

import java.util.function.Predicate;

import static org.asmus.builder.IntrospectedEventFactory.*;


@Getter
@RequiredArgsConstructor
public enum EQualifiedSceneDict {

    //order matters!
    MODIFIED(MODIFIER, Behavioral::hasModifiersAssigned),
    MULTI_CLICK(MULTIPLICITY, Behavioral::hasClickMultiplicity),
    LONG_CLICK(LONG, Behavioral::isLongPress),
    FAST_CLICK(PUSH, q -> true),

    ;

    final ActuationBehaviour behaviour;
    final Predicate<Behavioral> predicate;
}
