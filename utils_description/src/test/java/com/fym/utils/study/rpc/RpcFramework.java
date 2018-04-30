package com.fym.utils.study.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by fengyiming on 2018/4/26.
 */
public class RpcFramework {

    public static void startRpc(Object service, int port) throws Exception {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Export service " + service.getClass().getName() + " on port " + port);
        while (true) {
            final Socket socket = server.accept();
            new Thread(() -> {
                try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
                    String methodName = input.readUTF();
                    Class<?>[] parametertypes = (Class<?>[]) input.readObject();
                    Object[] arguments = (Object[]) input.readObject();
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    try {
                        Method method = service.getClass().getMethod(methodName, parametertypes);
                        Object result = method.invoke(service, arguments);
                        outputStream.writeObject(result);
                    } catch (Throwable e) {
                        outputStream.writeObject(e);
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static <T> T refer(Class<T> interfaceClass, String host, int port) throws Exception {

        System.out.println(
                "Get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, (proxy, method, arguments) -> {
            try (Socket socket = new Socket(host, port); ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
                outputStream.writeUTF(method.getName());
                outputStream.writeObject(method.getParameterTypes());
                outputStream.writeObject(arguments);
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                Object result = inputStream.readObject();
                if (result instanceof Throwable) {
                    throw (Throwable) result;
                }
                return result;
            } catch (Throwable e) {
                throw e;
            }
        });
    }
}
