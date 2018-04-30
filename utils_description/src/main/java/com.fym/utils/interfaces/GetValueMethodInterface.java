package com.fym.utils.interfaces;

/**
 * Created by fengyiming on 2018/1/24.
 * 获取从db里得到的值
 */
@FunctionalInterface
public interface GetValueMethodInterface<T> {

    /**
     * 编写从其他地方获取value的方法
     * @return
     * @throws Exception
     */
    T getValue() throws Exception;
}
