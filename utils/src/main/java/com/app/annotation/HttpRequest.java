package com.app.annotation;

import com.app.http.app.DefaultParamsBuilder;
import com.app.http.app.ParamsBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Haru on 2016/3/29 0029.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRequest {
    String host() default "";
    String path();
    Class<? extends ParamsBuilder> builder() default DefaultParamsBuilder.class;
    String[] signs() default "";
    String[] cacheKeys() default "";
}
