package com.fym.utils.interfaces;

/**
 * Created by fengyiming on 2018/1/24.
 * 不关心返回值执行的方法
 */
@FunctionalInterface
public interface VoidTaskMethodInterface<T> {

    /**
     * 多线程拆分list参数单个执行
     *
     * @param t
     */
    void exec(T t) throws Exception;
}
