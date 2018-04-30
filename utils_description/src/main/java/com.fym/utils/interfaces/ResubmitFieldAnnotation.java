package com.fym.utils.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by fengyiming on 2018/4/26.
 * 只能在8大基础类型上使用此注解，不然会计算错误
 */
@Target({ METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
@Documented
public @interface ResubmitFieldAnnotation {

}
