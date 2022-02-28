package rpc.transport.socket.server;

import enumeration.RpcError;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import rpc.hook.ShutdownHook;
import rpc.provider.ServiceProvider;
import rpc.provider.ServiceProviderImpl;
import rpc.registry.NacosServiceRegistry;
import rpc.registry.ServiceRegistry;
import rpc.transport.AbstractRpcServer;
import rpc.transport.RpcServer;
import rpc.handler.RequestHandler;
import rpc.serializer.CommonSerializer;
import factory.ThreadPoolFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Socket方式远程方法调用的提供者（服务端）
 *
 * @author 3aum0n
 */
@Slf4j
public class SocketServer extends AbstractRpcServer {

    private final ExecutorService threadPool;
    private static final String THREAD_NAME_PREFIX = "socket-rpc-server";

    private final RequestHandler requestHandler = new RequestHandler();
    private final CommonSerializer serializer;

    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanServices();
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(host, port));
            log.info("服务器启动...");
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                log.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("服务器启动过程中发生错误", e);
        }
    }

}
