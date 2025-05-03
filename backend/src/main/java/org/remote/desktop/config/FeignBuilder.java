package org.remote.desktop.config;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.controller.SceneApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.winder.api.WinderConstants;
import org.winder.api.WinderNativeConnectorApi;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class FeignBuilder {

    @Value("${api.prefix}")
    private String prefix;

    public SceneApi buildGpadApiClient(String url) {
        String uri = UriComponentsBuilder.newInstance()
                .uri(URI.create(url))
                .pathSegment(prefix)
                .build()
                .toString();

        System.out.println("Building feign client with url: " + uri);

        return Feign.builder()
                .logger(new Slf4jLogger(SceneApi.class))
                .contract(new SpringMvcContract())
                .target(SceneApi.class, uri);
    }

    public WinderNativeConnectorApi buildWinderNativeConnectorApiClient(String host) {
        String uri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(host)
                .port(8080)
                .pathSegment(WinderConstants.API_PREFIX)
                .build()
                .toString();

        System.out.println("Building WinderNativeConnectorApi client with url: " + uri);

        return Feign.builder()
                .logger(new Slf4jLogger(WinderNativeConnectorApi.class))
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .contract(new SpringMvcContract())
                .target(WinderNativeConnectorApi.class, uri);
    }
}
