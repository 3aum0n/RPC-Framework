package test;

import api.HelloObject;
import api.HelloService;
import rpc.RpcClientProxy;
import rpc.serializer.KryoSerializer;
import rpc.socket.client.SocketClient;

import java.util.Random;

/**
 * @author 3aum0n
 */
public class TestSocketClient {
    /**
     * 通过动态代理，生成代理对象，并且调用，动态代理会自动帮我们向服务端发送请求
     */
    public static void main(String[] args) {
        SocketClient client = new SocketClient("127.0.0.1", 9000);
        client.setSerializer(new KryoSerializer());
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject message = new HelloObject(new Random().nextInt(10), "This is a message");
        String res = helloService.hello(message);
        System.out.println(res);
    }
}
