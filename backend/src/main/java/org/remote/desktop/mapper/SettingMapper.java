package org.remote.desktop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.remote.desktop.db.entity.Setting;
import org.remote.desktop.model.dto.SettingDto;
import org.remote.desktop.model.vto.SettingVto;
import org.remote.desktop.property.SettingsProperties;

@Mapper(componentModel = "spring")
public interface SettingMapper {

    SettingDto map(Setting entity);

    Setting map(SettingVto vto);

    SettingVto mapVto(Setting setting);

    @Mapping(target = "id", ignore = true)
    Setting map(SettingDto dto);

    @Mapping(target = "id", ignore = true)
    Setting map(SettingsProperties props);

    SettingDto mapProps(SettingsProperties props);
}
