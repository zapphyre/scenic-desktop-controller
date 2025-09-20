package org.remote.desktop.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.asmus.model.EQualificationType;
import org.remote.desktop.model.Behavioral;

import java.util.function.Predicate;


@Getter
@RequiredArgsConstructor
public enum EQualifiedSceneDict {

    //order matters!
    MULTI_CLICK(EQualificationType.MULTIPLE, Behavioral::hasClickMultiplicity),
    LONG_CLICK(EQualificationType.LONG, Behavioral::isLongPress),
    MODIFIED(EQualificationType.RELEASE, Behavioral::hasModifiersAssigned),
    FAST_CLICK(EQualificationType.PUSH, q -> true),
    ;

    final EQualificationType qualifierType;
    final Predicate<Behavioral> predicate;
}
