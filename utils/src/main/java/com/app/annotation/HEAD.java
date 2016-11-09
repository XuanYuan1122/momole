package com.app.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Created by Haru on 2016/4/19 0019.
 */
@Documented
@Target(PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface HEAD {
    String value() default "";
}
