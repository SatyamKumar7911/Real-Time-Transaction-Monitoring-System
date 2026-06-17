package com.example.accountservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transaction Management Aspect - Handles transaction cross-cutting concern
 * Provides monitoring and logging for transactional operations
 */
@Aspect
@Component
@Slf4j
public class TransactionAspect {

    /**
     * Pointcut for all methods annotated with @Transactional
     */
    @Pointcut("@annotation(transactional)")
    public void transactionalMethods(Transactional transactional) {}

    /**
     * Around advice for transaction monitoring
     */
    @Around("transactionalMethods(transactional)")
    public Object monitorTransaction(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("🔄 Transaction started for method: {} [readOnly={}]", 
                methodName, transactional.readOnly());
        
        long startTime = System.currentTimeMillis();
        Object result;
        
        try {
            result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("✅ Transaction committed successfully for method: {} in {} ms", 
                    methodName, executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("❌ Transaction rolled back for method: {} after {} ms due to: {}", 
                    methodName, executionTime, e.getMessage());
            throw e;
        }
        
        return result;
    }
}
