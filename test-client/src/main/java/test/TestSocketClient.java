package test;

import api.HelloObject;
import api.HelloService;
import rpc.serializer.CommonSerializer;
import rpc.transport.RpcClient;
import rpc.transport.RpcClientProxy;
import rpc.serializer.KryoSerializer;
import rpc.transport.socket.client.SocketClient;

import java.util.Random;

/**
 * @author 3aum0n
 */
public class TestSocketClient {
    /**
     * 通过动态代理，生成代理对象，并且调用，动态代理会自动帮我们向服务端发送请求
     */
    public static void main(String[] args) {
        RpcClient client = new SocketClient(CommonSerializer.JSON_SERIALIZER);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject message = new HelloObject(new Random().nextInt(10), "This is a message");
        String res = helloService.hello(message);
        System.out.println(res);
    }
}
