package com.fym.utils.study.rpc;

/**
 * Created by fengyiming on 2018/4/26.
 */
public class RpcProvider {

    public static void main(String[] args) throws Exception {
        HelloService helloService = new HelloServiceImpl();
        RpcFramework.startRpc(helloService,1234);
    }
}
