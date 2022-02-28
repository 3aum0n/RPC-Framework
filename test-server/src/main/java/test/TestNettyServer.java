package test;

import api.HelloService;
import rpc.annotation.ServiceScan;
import rpc.serializer.CommonSerializer;
import rpc.transport.RpcServer;
import rpc.transport.netty.server.NettyServer;
import rpc.serializer.ProtobufSerializer;

/**
 * @author 3aum0n
 */
@ServiceScan
public class TestNettyServer {
    public static void main(String[] args) {
        RpcServer server = new NettyServer("127.0.0.1", 9000, CommonSerializer.HESSIAN_SERIALIZER);
        server.start();
    }
}
