package test;

import api.HelloObject;
import api.HelloService;
import client.RpcClientProxy;

import java.util.Random;

/**
 * @author 3aum0n
 * @create 2022-01-08 18:19
 */
public class TestClient {
    /**
     * 通过动态代理，生成代理对象，并且调用，动态代理会自动帮我们向服务端发送请求
     */
    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject message = new HelloObject(new Random().nextInt(10), "This is a message");
        String res = helloService.hello(message);
        System.out.println(res);
    }
}
