package server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author 3aum0n
 * @create 2022-01-07 23:29
 */
@Slf4j
public class RpcServer {

    private final ExecutorService threadPool;

    /**
     * 使用线程池创建线程
     */
    public RpcServer() {
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    public void register(Object service, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("服务器正在启动");
            Socket socket;
            while (null != (socket = serverSocket.accept())) {
                log.info("服务器连接成功, Ip为: " + socket.getInetAddress());
                // 向工作线程WorkerThread传入了socket和用于服务端实例service
                threadPool.execute(new WorkerThread(socket, service));
            }
        } catch (IOException e) {
            log.error("连接时有错误发生", e);

        }
    }
}
