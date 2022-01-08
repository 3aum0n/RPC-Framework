package test;

import api.HelloService;
import server.RpcServer;

/**
 * @author 3aum0n
 * @create 2022-01-08 18:15
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService, 9000);
    }
}
