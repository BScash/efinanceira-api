package br.com.bscash.efinanceira.application.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    
    @Pointcut("execution(* br.com.bscash.efinanceira.application.controller..*(..))")
    public void controllerMethods() {}
    
    @Pointcut("execution(* br.com.bscash.efinanceira.domain.service..*(..))")
    public void serviceMethods() {}
    
    @Pointcut("execution(* br.com.bscash.efinanceira.infrastructure.repository..*(..))")
    public void repositoryMethods() {}
    
    @Around("controllerMethods() || serviceMethods() || repositoryMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.debug("Iniciando execução: {}.{}() com argumentos: {}", className, methodName, args);
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();
            
            log.debug("Concluída execução: {}.{}() em {}ms", className, methodName, stopWatch.getTotalTimeMillis());
            
            return result;
        } catch (Exception e) {
            stopWatch.stop();
            log.error("Erro na execução: {}.{}() em {}ms - {}", 
                className, methodName, stopWatch.getTotalTimeMillis(), e.getMessage(), e);
            throw e;
        }
    }
}
