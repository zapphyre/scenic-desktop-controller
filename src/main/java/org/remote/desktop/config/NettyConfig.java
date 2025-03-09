package org.remote.desktop.config;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyConfig {

    @Bean
    public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> nettyCustomizer() {
        return factory -> factory.addServerCustomizers(server -> server.tcpConfiguration(tcp -> tcp
                .option(io.netty.channel.ChannelOption.TCP_NODELAY, true)
                .option(io.netty.channel.ChannelOption.SO_SNDBUF, 128) // 1MB send buffer
                .option(io.netty.channel.ChannelOption.SO_RCVBUF, 128) // 1MB receive buffer
        ));
    }
}
