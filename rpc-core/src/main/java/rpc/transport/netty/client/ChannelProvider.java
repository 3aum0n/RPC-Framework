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
import java.util.Map;
import java.util.concurrent.*;

/**
 * 用于获取 Channel 对象
 *
 * @author 3aum0n
 */
@Slf4j
public class ChannelProvider {

    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();

    private static Map<String, Channel> channels = new ConcurrentHashMap<>();

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) throws InterruptedException {
        String key = inetSocketAddress.toString() + serializer.getCode();
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if (channels != null && channel.isActive()) {
                return channel;
            } else {
                channels.remove(key);
            }
        }
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
        Channel channel = null;
        try {
            channel = connect(bootstrap, inetSocketAddress);
        } catch (ExecutionException e) {
            log.error("连接客户端时有错误发生:", e);
            return null;
        }
        channels.put(key, channel);
        return channel;
    }

    private static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("客户端连接成功");
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
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
