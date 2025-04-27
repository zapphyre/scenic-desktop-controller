package org.remote.desktop.ui.model;

import org.asmus.model.EButtonAxisMapping;

import java.util.Set;

@FunctionalInterface
public interface ModifiedIndexedTransformer {

    IndexLetterAction modifiedBy(Set<EButtonAxisMapping> modifiers);
}

