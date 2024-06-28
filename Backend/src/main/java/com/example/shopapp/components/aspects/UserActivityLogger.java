package com.example.shopapp.components.aspects;

import java.util.logging.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Aspect
public class UserActivityLogger {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    // named pointcut
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    @Around("controllerMethods() && execution(* com.example.shopapp.controllers.UserController.*(..))")
    public Object logUserActivity(ProceedingJoinPoint joinPoint) throws Throwable {
        // write log before method execution
        String methodName = joinPoint.getSignature().getName();
        String remoteAddress = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getRemoteAddr();

        // So use StringBuilder for join String long, basic should use + operator. To optimize performance

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("User activity started: ");
        logMessage.append(methodName);
        logMessage.append(" from IPAddress: ");
        logMessage.append(remoteAddress);
        logger.info(logMessage.toString());

        // execute the method original
        Object rs = joinPoint.proceed();

        // write log after method execution
        logger.info("User activity finished: " + methodName);
        return rs;
    }
}
