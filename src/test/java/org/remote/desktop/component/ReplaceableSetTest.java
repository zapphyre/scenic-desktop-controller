package org.remote.desktop.component;


import org.asmus.model.EButtonAxisMapping;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ReplaceableSetTest {

    @Test
    void canReplaceAll() {
        ReplaceableSet<EButtonAxisMapping> set = new ReplaceableSet<>(Set.of(EButtonAxisMapping.A, EButtonAxisMapping.B, EButtonAxisMapping.UP));

        set.replaceAll(Set.of(EButtonAxisMapping.A));

        Assertions.assertEquals(1, set.size());
    }
}
