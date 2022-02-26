package test;

import api.HelloService;
import rpc.serializer.HessianSerializer;
import rpc.transport.socket.server.SocketServer;

/**
 * @author 3aum0n
 */
public class TestSocketServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        SocketServer rpcServer = new SocketServer("127.0.0.1", 9000);
        rpcServer.setSerializer(new HessianSerializer());
        rpcServer.publishService(helloService, HelloService.class);
    }
}
