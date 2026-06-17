package com.example.accountservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Security Audit Aspect - Logs security-related events
 * Tracks API access, user authentication, and authorization attempts
 */
@Aspect
@Component
@Slf4j
public class SecurityAuditAspect {

    /**
     * Pointcut for all controller methods
     */
    @Pointcut("execution(* com.example.accountservice.controller..*(..))")
    public void controllerMethods() {}

    /**
     * Log all API access attempts with security context
     */
    @Before("controllerMethods()")
    public void auditApiAccess(JoinPoint joinPoint) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                log.info("🔐 Security Audit - API Access: {} {} from IP: {} | Method: {}.{}() | Args: {}",
                        request.getMethod(),
                        request.getRequestURI(),
                        request.getRemoteAddr(),
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(),
                        Arrays.toString(joinPoint.getArgs()));
            }
        } catch (Exception e) {
            log.debug("Unable to capture request context: {}", e.getMessage());
        }
    }
}
