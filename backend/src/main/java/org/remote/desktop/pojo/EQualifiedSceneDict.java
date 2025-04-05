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
    MODIFIED(EQualificationType.RELEASE, Behavioral::hasModifiersAssigned),
    LONG_CLICK(EQualificationType.LONG, Behavioral::isLongPress),  // b/c when i'm modifying press, it can come later than simple long
    FAST_CLICK(EQualificationType.PUSH, q -> true),

    ;

    final EQualificationType qualifierType;
    final Predicate<Behavioral> predicate;
}
