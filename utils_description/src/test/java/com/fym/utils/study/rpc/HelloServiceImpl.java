package com.fym.utils.study.rpc;

/**
 * @author chenxun created at 2018/3/16
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String say) {
        return "helloImpl" + say;
    }
}
