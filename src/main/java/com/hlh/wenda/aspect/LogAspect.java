package com.hlh.wenda.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.hlh.wenda.controller.IndexController.*(..))")
    public void beforeMethod(){
        logger.info("before Method" + new Date());

    }

    @After("execution(* com.hlh.wenda.controller.IndexController.*(..))")
    public void afterMethod(){
        logger.info("after Method" + new Date());

    }

}
