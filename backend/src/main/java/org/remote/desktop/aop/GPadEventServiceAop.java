package org.remote.desktop.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(name = "aop.logging", havingValue = "true")
public class GPadEventServiceAop {

    @Pointcut("execution(public * org.remote.desktop.service.*.*(..))")
    void allServiceMethodsPointcut(){}

    @Pointcut("execution(public * org.remote.desktop.db.*.*.*(..))")
    void allDbMethodsPointcut(){}

    @Around("allServiceMethodsPointcut()")
    Object aroundServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return processAndLog(joinPoint);
    }

    @Around("allDbMethodsPointcut()")
    Object aroundDbMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return processAndLog(joinPoint);
    }

    Object processAndLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("===== aop {} begin =====", joinPoint.getSignature().getName());

        log.debug("param: {}", joinPoint.getArgs());
        Object result = joinPoint.proceed();
        log.debug("result: {}", result);

        log.debug("===== aop {} end =====", joinPoint.getSignature().getName());

        return result;
    }
}
