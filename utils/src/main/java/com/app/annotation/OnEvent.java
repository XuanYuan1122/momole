package com.app.annotation;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * event annotation
 * Created by Haru on 2016/3/28 0028.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnEvent {
    int[] value();
    int[] parentId() default 0;
    Class<?> type() default View.OnClickListener.class;
    String setter() default "";
    String method() default "";
}
