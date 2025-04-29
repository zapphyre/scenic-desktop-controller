package org.remote.desktop.config;

import feign.Contract;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.RequiredArgsConstructor;
import org.remote.desktop.controller.SceneApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Component
//@EnableFeignClients
@RequiredArgsConstructor
public class FeignBuilder {

    @Value("${api.prefix}")
    private String prefix;

    public SceneApi buildApiClient(String url) {
        String uri = UriComponentsBuilder.newInstance()
                .host(url)
                .pathSegment(prefix)
                .build()
                .toString();

        System.out.println("Building feign client with url: " + uri);

        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger(SceneApi.class))
                .contract(new SpringMvcContract())
                .target(SceneApi.class, uri);
    }
}
