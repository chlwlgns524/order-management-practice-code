package com.example.kdt.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(com.example.kdt.aop.TrackTime)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Before method called. {}", joinPoint.getSignature().toString());
        long startTime = System.nanoTime();
        Object result = joinPoint.proceed();
        long endTime = System.nanoTime();
        logger.info("After method called with result => {} and time => {}", result, endTime - startTime);

        return result;
    }

}
