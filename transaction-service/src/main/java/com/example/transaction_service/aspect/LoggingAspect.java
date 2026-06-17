package com.example.transaction_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Logging Aspect - Handles logging cross-cutting concern using Spring AOP
 * Logs method entry, exit, exceptions, and execution time
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut for all methods in service layer
     */
    @Pointcut("execution(* com.example.transaction_service.service..*(..))")
    public void serviceLayer() {}

    /**
     * Pointcut for all methods in controller layer
     */
    @Pointcut("execution(* com.example.transaction_service.controller..*(..))")
    public void controllerLayer() {}

    /**
     * Pointcut for all methods in repository layer
     */
    @Pointcut("execution(* com.example.transaction_service.repository..*(..))")
    public void repositoryLayer() {}

    /**
     * Before advice - logs method entry with parameters
     */
    @Before("serviceLayer() || controllerLayer()")
    public void logBefore(JoinPoint joinPoint) {
        log.info(">>> Entering method: {}.{}() with arguments: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * AfterReturning advice - logs successful method completion with return value
     */
    @AfterReturning(pointcut = "serviceLayer() || controllerLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("<<< Method completed successfully: {}.{}() returned: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result);
    }

    /**
     * AfterThrowing advice - logs exceptions thrown by methods
     */
    @AfterThrowing(pointcut = "serviceLayer() || controllerLayer() || repositoryLayer()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("!!! Exception in method: {}.{}() with message: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                exception.getMessage(),
                exception);
    }

    /**
     * Around advice - logs execution time and wraps method execution
     */
    @Around("serviceLayer() || controllerLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        Object result;
        try {
            result = joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("⏱ Method {}.{}() executed in {} ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    executionTime);
        }
        
        return result;
    }
}
