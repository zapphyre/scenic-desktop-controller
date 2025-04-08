package org.remote.desktop.text.translator;

import org.asmus.model.PolarCoords;

@FunctionalInterface
public interface PolarCoordsSectionTranslator {
    int translate(PolarCoords coords);
}