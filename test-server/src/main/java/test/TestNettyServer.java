package test;

import api.HelloService;
import rpc.transport.netty.server.NettyServer;
import rpc.serializer.ProtobufSerializer;

/**
 * @author 3aum0n
 */
public class TestNettyServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        NettyServer rpcServer = new NettyServer("127.0.0.1", 9000);
        rpcServer.setSerializer(new ProtobufSerializer());
        rpcServer.publishService(helloService, HelloService.class);
    }
}
