package test;

import api.HelloService;
import rpc.netty.server.NettyServer;
import rpc.registry.DefaultServiceRegistry;
import rpc.registry.ServiceRegistry;
import rpc.socket.server.SocketServer;

/**
 * @author 3aum0n
 */
public class TestNettyServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        NettyServer rpcServer = new NettyServer();
        rpcServer.start(9000);
    }
}
