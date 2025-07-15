package org.remote.desktop.mapper;

import org.asmus.model.PolarCoords;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.remote.desktop.model.RepeatablePolarCoords;

@Mapper(componentModel = "spring")
public interface PolarCoordsMapper {

    PolarCoords map(org.zapphyre.model.PolarCoords polarCoords);

    @InheritInverseConfiguration
    org.zapphyre.model.PolarCoords map(PolarCoords polarCoords);

    RepeatablePolarCoords mapRep(PolarCoords polarCoords);
}
