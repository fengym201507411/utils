package com.fym.utils.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by fengyiming on 2017/12/13.
 */
@Target({ TYPE})
@Retention(RUNTIME)
@Documented
public @interface ExcelSheet {

     String sheetName() default "";
}
