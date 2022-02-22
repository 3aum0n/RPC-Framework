package test;

import api.HelloObject;
import api.HelloService;
import rpc.client.RpcClientProxy;
import rpc.netty.client.NettyClient;
import rpc.socket.client.SocketClient;

import java.util.Random;

/**
 * @author 3aum0n
 */
public class TestNettyClient {
    /**
     * 通过动态代理，生成代理对象，并且调用，动态代理会自动帮我们向服务端发送请求
     */
    public static void main(String[] args) {
        NettyClient client = new NettyClient("127.0.0.1", 9000);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject message = new HelloObject(new Random().nextInt(10), "This is a message");
        String res = helloService.hello(message);
        System.out.println(res);
    }
}
