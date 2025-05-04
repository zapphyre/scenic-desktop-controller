package org.remote.desktop.config;

import feign.*;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.remote.desktop.controller.SceneApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.winder.api.WinderConstants;
import org.winder.api.WinderNativeConnectorApi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.time.Duration;
import java.util.Map;

@Slf4j
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
                .logLevel(feign.Logger.Level.FULL)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .contract(new SpringMvcContract())
                .invocationHandlerFactory(new CustomInvocationHandlerFactory())
                .errorDecoder(new SuppressRetryableErrorDecoder())
                .retryer(Retryer.NEVER_RETRY) // Disable retries
                .options(new Request.Options(Duration.ofMillis(420), Duration.ofMillis(420), false))
                .target(WinderNativeConnectorApi.class, uri);
    }

    public static class CustomInvocationHandlerFactory implements InvocationHandlerFactory {

        @Override
        public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
            return (proxy, method, args) -> {
                try {
                    return dispatch.get(method).invoke(args);
                } catch (feign.RetryableException e) {
                    log.error("Suppressed RetryableException in InvocationHandler: {}", e.getMessage());
                    return null; // Suppress the exception
                }
            };
        }
    }

    public static class SuppressRetryableErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, Response response) {
            // Network errors (e.g., Connection refused) typically have no response
            if (response == null) {
                System.err.println("Suppressed network error for " + methodKey + ": Connection refused");
                return null; // Suppress the exception
            }

            // Decode the response using the default decoder
            Exception exception;
            try {
                exception = new ErrorDecoder.Default().decode(methodKey, response);
            } catch (Exception e) {
                exception = e;
            }

            // Suppress RetryableException
            if (exception instanceof feign.RetryableException) {
                System.err.println("Suppressed RetryableException for " + methodKey + ": " + exception.getMessage());
                return null; // Suppress the exception
            }

            // Return other exceptions (or suppress all by returning null, depending on your needs)
            return exception;
        }
        }
}
