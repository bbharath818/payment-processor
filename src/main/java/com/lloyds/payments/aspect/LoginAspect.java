package com.lloyds.payments.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoginAspect {

    Logger logger = LoggerFactory.getLogger(LoginAspect.class);

    @Pointcut("execution(* com.lloyds.payments.*.*.*(..))")
    public void PaymentIngestor(){
    }

    @Around("PaymentIngestor()")
    public Object loginPaymentIngestor(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed(); // Executes the actual method
        long executionTime = System.currentTimeMillis() - start;
        logger.info("{} executed in {}ms{}", joinPoint.getSignature(), executionTime, proceed);
        return proceed; // Return the actual result from the method
    }

}