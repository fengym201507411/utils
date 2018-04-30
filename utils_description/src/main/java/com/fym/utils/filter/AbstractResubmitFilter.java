package com.fym.utils.filter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * Created by fengyiming on 2018/4/26.
 *
 * 用法参考ResubmitFilter
 */
public abstract class AbstractResubmitFilter {

    /**
     * 分隔符
     */
    private static String SPLIT = "_";

    /**
     * 初始计数
     */
    private static Integer INIT_COUNT = 1;

    /**
     * 空对象的替代字符
     */
    private static String  NULL_OBJECT = "null";

    /**
     * 短时间内重复提交判断（目前只支持方法的入参参与校验，不能递归深度参与校验）
     * 需要继承该类然后调用此方法，传入本次请求的request和redisCache,然后可按返回结果去抛回异常ExceptionEnum.RESUBMIT_ERROR
     *
     * @return
     */
    public <R extends RedisCache> boolean resubmitCheck(Request request, R r) {
        Method method = request.getDeclaredMethod();
        Map<String, Object> parametersMap = request.getParameters();
        if (method.isAnnotationPresent(ResubmitCheckAnnotation.class)) {
            ResubmitCheckAnnotation resubmitCheckAnnotation = method.getAnnotation(ResubmitCheckAnnotation.class);
            Parameter[] parameters = method.getParameters();
            //判断是否需要参数校验
            boolean needCheck = false;
            for (Parameter parameter : parameters) {
                if (parameter.isAnnotationPresent(ResubmitFieldAnnotation.class)) {
                    needCheck = true;
                    break;
                }
            }
            //判断请求的方法里有没有带需要做防重复提交的参数
            if (!needCheck) {
                return true;
            }
            //
            StringBuffer key = new StringBuffer();
            key.append(request.getImplementedClass().getCanonicalName()).append(SPLIT).append(method.getName());
            for (Parameter parameter : parameters) {
                if (parameter.isAnnotationPresent(ResubmitFieldAnnotation.class)) {
                    Object parameterValue = parametersMap.get(parameter.getName());
                    key.append(SPLIT).append(parameter.getName()).append(SPLIT);
                    if (parameterValue == null) {
                        key.append(NULL_OBJECT);
                    } else {
                        key.append(parameterValue.toString());
                    }
                }
            }
            Integer value = r.get(key.toString());
            if (value != null) {
                return false;
            }
            r.set(key.toString(), resubmitCheckAnnotation.lockMills(), INIT_COUNT);
        }
        return true;
    }
}
