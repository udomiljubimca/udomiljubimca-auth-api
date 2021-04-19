package com.auth.testlogin.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
public class LoggingInterceptor {

    @Before(value = "@annotation(com.auth.testlogin.logging.Loggable)")
    public void beforeMethodExecution(JoinPoint joinPoint) {
        LoggerFactory.getLogger(joinPoint.getTarget().getClass()).info("Method-[{}] Input-{}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterReturning(value = "@annotation(com.auth.testlogin.logging.Loggable)", returning = "returnValue")
    public void afterMethodExecution(JoinPoint joinPoint, Object returnValue) {
        LoggerFactory.getLogger(joinPoint.getTarget().getClass()).info("Method-[{}] Input-{} Return-[{}]", joinPoint.getSignature(), joinPoint.getArgs(), returnValue);
    }

    @AfterThrowing(pointcut = "@annotation(com.auth.testlogin.logging.Loggable)", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        LoggerFactory.getLogger(joinPoint.getTarget().getClass()).error("Method-[{}] Input-{} ERROR: {}", joinPoint.getSignature(), joinPoint.getArgs(), ex.getMessage());
    }

}
