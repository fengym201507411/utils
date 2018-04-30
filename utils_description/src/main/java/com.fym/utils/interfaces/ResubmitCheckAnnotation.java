package com.fym.utils.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by fengyiming on 2018/4/26.
 */
@Target({ METHOD})
@Retention(RUNTIME)
@Documented
public @interface ResubmitCheckAnnotation {

    /**
     * 锁定时间单位（单位s）
     * @return
     */
    int lockMills() default 1;
}
