package org.remote.desktop.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequestCacheAop {

    private final CacheManager cacheManager;

    // Pointcut for all controller methods in your package
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMappingMethods() {
    }

    @Pointcut("@annotation(org.remote.desktop.mark.CacheEvictAll)")
    public void evictAll() {
    }

    // Pointcut for methods with POST, PUT, PATCH, DELETE annotations
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void modifyingMethods() {
    }

    // Combine pointcuts: all controller methods except GET
    @Pointcut("restControllerMethods() && modifyingMethods() && !getMappingMethods()")
    public void nonGetControllerMethods() {
    }

    @AfterReturning(pointcut = "nonGetControllerMethods() || evictAll()", returning = "result")
    public void clearAllCaches(Object result) {
        // Handle both Mono/Flux and synchronous returns
        clearCaches();
    }

    void clearCaches() {
        Optional.ofNullable(cacheManager)
                .map(CacheManager::getCacheNames)
                .orElseGet(Collections::emptyList).stream()
                .map(Objects.requireNonNull(cacheManager)::getCache)
                .filter(Objects::nonNull)
                .peek(q -> log.info("Clearing cache: '{}'", q.getName()))
                .forEach(Cache::clear);
    }
}
