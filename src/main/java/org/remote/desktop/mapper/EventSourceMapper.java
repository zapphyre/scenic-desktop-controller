package org.remote.desktop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.remote.desktop.model.WebSourceDef;
import org.remote.desktop.source.ConnectableSource;
import org.remote.desktop.source.impl.WebSource;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import java.net.URI;

@Mapper(componentModel = "spring")
public interface EventSourceMapper {

    @Mapping(target = "port", source = "info")
    @Mapping(target = "baseURI", source = "info")
    WebSourceDef map(ServiceEvent event);

    default URI map(ServiceInfo info) {
        return URI.create(info.getHostAddresses()[0]);
    }

    default int mapPort(ServiceInfo info) {
        return info.getPort();
    }

    default String mapName(ServiceEvent event) {
        return event.getName();
    }
}
