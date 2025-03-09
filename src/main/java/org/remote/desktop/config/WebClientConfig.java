package org.remote.desktop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

//@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.RequestHeadersUriSpec<?> getBuilder(@Value("${remote.host.baseurl:http://localhost:9090}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build()
                .get();
    }
}
