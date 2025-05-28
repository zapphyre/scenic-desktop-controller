package org.remote.desktop.mapper;

import lombok.SneakyThrows;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.remote.desktop.db.entity.Setting;
import org.remote.desktop.model.dto.SettingDto;
import org.remote.desktop.model.vto.SettingVto;
import org.remote.desktop.property.SettingsProperties;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface SettingMapper {

    @Mapping(target = "hintedIpAddress", expression = "java(getLocalIpAddress().toString().substring(1))")
    @Mapping(target = "ipSetManually", expression = "java(java.util.Objects.nonNull(entity.getIpAddress()))")
    @Mapping(target = "ipAddress", source = "ipAddress", qualifiedByName = "ipAddr")
    SettingDto map(Setting entity);

    Setting map(SettingVto vto);

    SettingVto mapVto(Setting setting);

    @Mapping(target = "id", ignore = true)
    Setting map(SettingDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "settingsInstance", constant = Setting.INST_NAME)
    Setting map(SettingsProperties props);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hintedIpAddress", ignore = true)
    @Mapping(target = "ipAddress", source = "ipAddress", qualifiedByName = "ipAddr")
    @Mapping(target = "ipSetManually", expression = "java(java.util.Objects.nonNull(props.getIpAddress()))")
    SettingDto mapProps(SettingsProperties props);

    @Named("ipAddr")
    default String mapIp(String ip) {
        return Optional.ofNullable(ip).orElseGet(() -> getLocalIpAddress().toString().substring(1));
    }

    @SneakyThrows
    default InetAddress getLocalIpAddress() {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (ni.isLoopback() || !ni.isUp() || ni.isVirtual()) continue;
            Enumeration<InetAddress> addrs = ni.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                if (addr instanceof Inet4Address && !addr.isLoopbackAddress() && !addr.isLinkLocalAddress()) {
                    return addr;
                }
            }
        }

        return InetAddress.getLocalHost();
    }
}
