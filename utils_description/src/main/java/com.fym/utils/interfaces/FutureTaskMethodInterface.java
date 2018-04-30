package com.fym.utils.interfaces;

/**
 * Created by fengyiming on 2018/1/24.
 * 有返回值的多线程拆分执行
 */
@FunctionalInterface
public interface FutureTaskMethodInterface<T, F> {

    /**
     * 多线程拆分list参数单个执行
     *
     * @param t
     * @return
     */
    F exec(T t) throws Exception;
}
