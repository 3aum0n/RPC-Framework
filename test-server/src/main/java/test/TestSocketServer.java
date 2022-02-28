package test;

import api.HelloService;
import rpc.annotation.ServiceScan;
import rpc.serializer.CommonSerializer;
import rpc.serializer.HessianSerializer;
import rpc.transport.RpcServer;
import rpc.transport.socket.server.SocketServer;

/**
 * @author 3aum0n
 */
@ServiceScan
public class TestSocketServer {
    public static void main(String[] args) {
        RpcServer server = new SocketServer("127.0.0.1", 9999, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }
}
