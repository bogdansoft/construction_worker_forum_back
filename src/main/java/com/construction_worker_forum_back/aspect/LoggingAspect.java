package com.construction_worker_forum_back.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Aspect
public class LoggingAspect {

    @Around("com.construction_worker_forum_back.aspect.Pointcuts.getAllValuesMethodsPointcut()")
    public Object logAroundGetAll(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("""
                        LOGGING
                        Enter: {}.{}()
                        """,
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        try {
            List<?> result = (List<?>) joinPoint.proceed();

            result.forEach(el ->
                    log.info("""
                                    Exit: {}.{}() with result = {}
                                    """,
                            joinPoint.getSignature().getDeclaringTypeName(),
                            joinPoint.getSignature().getName(),
                            el.toString()));

            return result;
        } catch (IllegalArgumentException e) {
            log.error("""
                            LOGGING
                            Illegal argument: {} in {}.{}()
                            """,
                    Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
            throw e;
        }
    }

    @Around("com.construction_worker_forum_back.aspect.Pointcuts.findValueByIdMethodsPointcut()" +
            "|| com.construction_worker_forum_back.aspect.Pointcuts.deleteValueMethodsPointcut()" +
            "|| com.construction_worker_forum_back.aspect.Pointcuts.createValueMethodsPointcut()" +
            "|| com.construction_worker_forum_back.aspect.Pointcuts.updateValueMethodsPointcut()")
    public Object logAroundAllOtherCases(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("""
                        LOGGING
                        Enter: {}.{}()
                        """,
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        try {
            var result = joinPoint.proceed();
            log.info("""
                            LOGGING
                            Exit: {}.{}() with result = {}
                            """,
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    result.toString());

            return result;
        } catch (IllegalArgumentException e) {
            log.error("""
                            LOGGING
                            Illegal argument: {} in {}.{}()
                            """,
                    Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
            throw e;
        }
    }
}
