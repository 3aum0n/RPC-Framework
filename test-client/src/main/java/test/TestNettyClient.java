package test;

import api.ByeService;
import api.HelloObject;
import api.HelloService;
import rpc.serializer.CommonSerializer;
import rpc.transport.RpcClient;
import rpc.transport.RpcClientProxy;
import rpc.transport.netty.client.NettyClient;
import rpc.serializer.HessianSerializer;

import java.util.Random;

/**
 * @author 3aum0n
 */
public class TestNettyClient {
    /**
     * 通过动态代理，生成代理对象，并且调用，动态代理会自动帮我们向服务端发送请求
     */
    public static void main(String[] args) {
        RpcClient client = new NettyClient(CommonSerializer.HESSIAN_SERIALIZER);
        RpcClientProxy proxy = new RpcClientProxy(client);

        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject message = new HelloObject(new Random().nextInt(10), "This is a message");
        System.out.println(helloService.hello(message));

        ByeService byeService = proxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("Netty"));
    }
}
