package test;

import api.HelloService;
import rpc.registry.DefaultServiceRegistry;
import rpc.registry.ServiceRegistry;
import rpc.serializer.HessianSerializer;
import rpc.socket.server.SocketServer;

/**
 * @author 3aum0n
 */
public class TestSocketServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        SocketServer rpcServer = new SocketServer(serviceRegistry);
        rpcServer.setSerializer(new HessianSerializer());
        rpcServer.start(9000);
    }
}
