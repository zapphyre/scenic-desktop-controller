package org.remote.desktop.mapper;

import org.asmus.model.PolarCoords;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper
public interface PolarCoordsMapper {

    PolarCoords map(org.zapphyre.model.PolarCoords polarCoords);

    @InheritInverseConfiguration
    org.zapphyre.model.PolarCoords map(PolarCoords polarCoords);
}
