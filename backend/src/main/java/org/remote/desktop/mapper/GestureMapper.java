package org.remote.desktop.mapper;

import org.mapstruct.Mapper;
import org.remote.desktop.db.entity.Gesture;
import org.remote.desktop.db.entity.GesturePath;
import org.remote.desktop.model.dto.GestureDto;
import org.remote.desktop.model.vto.GesturePathVto;
import org.remote.desktop.model.vto.GestureVto;

@Mapper(componentModel = "spring")
public interface GestureMapper {

    GestureVto map(Gesture gesture);

//    GestureDto mapDto(Gesture gesture);

    GesturePathVto map(GesturePath gesturePath);

    GestureDto mapDto(Gesture entity);

}
