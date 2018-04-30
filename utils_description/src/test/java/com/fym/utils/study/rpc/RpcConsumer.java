package com.fym.utils.study.rpc;

/**
 * Created by fengyiming on 2018/4/26.
 */
public class RpcConsumer {


    public static void main(String[] args) throws Exception {
        HelloService service = RpcFramework.refer(HelloService.class,"127.0.0.1",1234);
        String result = service.hello("123123");
        System.out.println(result);
    }
}
