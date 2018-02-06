package com.devexperts.chameleon.aspect;

/*-
 * #%L
 * Chameleon. Color Palette Management Tool
 * %%
 * Copyright (C) 2016 - 2018 Devexperts, LLC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile("timing")
public class TimingAspect {

    @Value("${logging.timing.bottom.timeline.ms}")
    private Integer timingMoreThen;

    private final static Logger logger = LoggerFactory.getLogger(TimingAspect.class);

    @Pointcut("execution(* com.devexperts.chameleon.*.*.*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {

        Signature signature = pjp.getSignature();
        String name = signature.toShortString();

        long start = System.currentTimeMillis();
        Object output = pjp.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        if (elapsedTime > timingMoreThen)
        logger.info("{} execution time: " + elapsedTime + " milliseconds.", name);
        return output;
    }

}
