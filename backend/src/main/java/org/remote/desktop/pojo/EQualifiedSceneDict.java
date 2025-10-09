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
    MODIFIED(EQualificationType.RELEASE, Behavioral::hasModifiersAssigned), // it means now modified command takes precedense when on scene with long defined too; long however fires after long hold and is processed as event
    LONG_CLICK(EQualificationType.LONG, Behavioral::isLongPress),
    FAST_CLICK(EQualificationType.PUSH, q -> true),
    ;

    final EQualificationType qualifierType;
    final Predicate<Behavioral> predicate;
}
