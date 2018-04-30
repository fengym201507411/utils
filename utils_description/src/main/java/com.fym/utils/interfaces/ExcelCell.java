package com.fym.utils.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by fengyiming on 2017/12/13.
 */
@Target({ FIELD})
@Retention(RUNTIME)
@Documented
public @interface ExcelCell {

    String cellHeadame() default "";

    int width() default 100;
}
