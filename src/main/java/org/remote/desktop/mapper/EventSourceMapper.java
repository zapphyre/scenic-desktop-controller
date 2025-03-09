package org.remote.desktop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.remote.desktop.model.WebSourceDef;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;

@Mapper(componentModel = "spring")
public interface EventSourceMapper {

    @Mapping(target = "port", source = "info")
    @Mapping(target = "baseUrl", source = "info")
    WebSourceDef map(ServiceEvent event);

    default String map(ServiceInfo info) {
        return info.getHostAddresses()[0];
    }

    default int mapPort(ServiceInfo info) {
//        return info.getPort();
        return 8081;
    }

    default String mapName(ServiceEvent event) {
        return event.getName();
    }
}
