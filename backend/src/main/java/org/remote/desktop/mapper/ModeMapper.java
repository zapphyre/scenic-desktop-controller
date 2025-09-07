package org.remote.desktop.mapper;

import org.mapstruct.Mapper;
import org.remote.desktop.db.entity.Mode;
import org.remote.desktop.model.vto.ModeVto;

@Mapper(componentModel = "spring")
public interface ModeMapper {

    ModeVto map(Mode entity);

}
