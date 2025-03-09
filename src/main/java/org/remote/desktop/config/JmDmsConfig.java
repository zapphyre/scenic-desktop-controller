package org.remote.desktop.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.component.SourceManager;
import org.remote.desktop.mapper.EventSourceMapper;
import org.remote.desktop.model.JmDmsInstanceName;
import org.remote.desktop.source.EventSourceListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jmdns.JmDNS;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JmDmsConfig {

    private final SourceManager sourceManager;
    private final JmDmsInstanceName name;

    @Value("${inet.address:none}")
    private String  myAddress;

    @Bean
    public JmDNS jmdns() throws IOException {
        InetAddress localHost = myAddress.equals("none") ? getLocalIpAddress() : InetAddress.getByName(myAddress);
        log.info("Local host: {}", localHost);
        return JmDNS.create(localHost);
    }

    @Bean
    public EventSourceListener eventSourceRepository(JmDNS jmdns, EventSourceMapper mapper) {
        log.info("JmDNS eventSourceRepository");
        return new EventSourceListener(jmdns, mapper, name, sourceManager::sourceDiscovered, sourceManager::sourceLost);
    }

    @SneakyThrows
    public static InetAddress getLocalIpAddress() {
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
