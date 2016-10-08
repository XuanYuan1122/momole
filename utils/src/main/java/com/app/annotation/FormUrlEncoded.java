package com.app.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Haru on 2016/4/19 0019.
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface FormUrlEncoded {
}
