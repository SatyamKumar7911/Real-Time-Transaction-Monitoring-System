package com.example.notification_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Performance Monitoring Aspect - Tracks method performance metrics
 * Identifies slow methods and potential performance bottlenecks
 */
@Aspect
@Component
@Slf4j
public class PerformanceMonitoringAspect {

    private static final long SLOW_METHOD_THRESHOLD_MS = 1000; // 1 second

    /**
     * Pointcut for all service methods
     */
    @Pointcut("execution(* com.example.notification_service.service..*(..))")
    public void serviceMethods() {}

    /**
     * Monitor performance and identify slow methods
     */
    @Around("serviceMethods()")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        
        Object result = joinPoint.proceed();
        
        long executionTime = System.currentTimeMillis() - startTime;
        
        if (executionTime > SLOW_METHOD_THRESHOLD_MS) {
            log.warn("🐌 SLOW METHOD DETECTED: {} took {} ms (threshold: {} ms)",
                    methodName, executionTime, SLOW_METHOD_THRESHOLD_MS);
        } else {
            log.debug("⚡ Performance: {} executed in {} ms", methodName, executionTime);
        }
        
        return result;
    }
}
