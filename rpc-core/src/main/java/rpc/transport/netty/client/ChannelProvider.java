package rpc.transport.netty.client;

import enumeration.RpcError;
import exception.RpcException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import rpc.codec.CommonDecoder;
import rpc.codec.CommonEncoder;
import rpc.serializer.CommonSerializer;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 用于获取 Channel 对象
 *
 * @author 3aum0n
 */
@Slf4j
public class ChannelProvider {

    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();

    private static final int MAX_RETRY_COUNT = 5;
    private static Channel channel = null;

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) {
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                /**
                 * 自定义序列化编解码器
                 * RpcResponse -> ByteBuf
                 */
                ch.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            connect(bootstrap, inetSocketAddress, countDownLatch);
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("获取 Channel 时有错误发生:", e);
        }
        return channel;
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, CountDownLatch countDownLatch) {
        connect(bootstrap, inetSocketAddress, MAX_RETRY_COUNT, countDownLatch);
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, int retry, CountDownLatch countDownLatch) {
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("客户端连接成功！");
                channel = future.channel();
                countDownLatch.countDown();
                return;
            }
            if (retry == 0) {
                log.error("客户端连接失败:重连次数已用完，放弃连接！");
                countDownLatch.countDown();
                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
            }
            int order = (MAX_RETRY_COUNT - retry) + 1;
            int delay = 1 << order;
            log.error("{}:连接失败, 第{}次重连...", new Date(), order);
            bootstrap.config().group().schedule(() -> connect(bootstrap, inetSocketAddress, retry - 1, countDownLatch),
                    delay, TimeUnit.SECONDS);
        });
    }

    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                /**
                 * 连接的超时时间，超过指定时间代表连接失败
                 */
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                /**
                 * 是否开启 TCP 底层心跳机制
                 */
                .option(ChannelOption.SO_KEEPALIVE, true)
                /**
                 * TCP 默认开启了 Nagle 算法, 该算法的作用是尽可能的发送大数据块，减少网络传输。
                 * TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法
                 */
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }

}
