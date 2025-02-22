package org.remote.desktop.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GPadEventServiceAop {

    @Pointcut("execution(public * org.remote.desktop.service.*.*(..))")
    void allServiceMethodsPointcut(){}

    @Before("execution(public * org.remote.desktop.service.GPadEventStreamService.getActuatorForScene(..))")
    void beforeMethod(JoinPoint joinPoint) {

    }
}
