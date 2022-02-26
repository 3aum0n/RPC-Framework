package rpc.netty.client;

import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.RpcError;
import exception.RpcException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import rpc.RpcClient;
import rpc.codec.CommonDecoder;
import rpc.codec.CommonEncoder;
import rpc.serializer.CommonSerializer;
import rpc.serializer.HessianSerializer;
import rpc.serializer.JsonSerializer;
import rpc.serializer.KryoSerializer;
import util.RpcMessageChecker;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * NIO 方式消费侧客户端类
 *
 * @author 3aum0n
 */
@Slf4j
public class NettyClient implements RpcClient {

    private static final Bootstrap bootstrap;

    private CommonSerializer serializer;

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    private String host;
    private int port;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
            Channel channel = ChannelProvider.get(new InetSocketAddress(host, port), serializer);
            if (channel.isActive()) {
                // 发送非阻塞，会立刻返回
                channel.writeAndFlush(rpcRequest).addListener(future -> {
                    if (future.isSuccess()) {
                        log.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        log.error("发送消息时有错误发生: ", future.cause());
                    }
                });
                channel.closeFuture().sync();
                // 通过 AttributeKey 的方式阻塞获得返回结果
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();
                RpcMessageChecker.check(rpcRequest, rpcResponse);
                result.set(rpcResponse.getData());
            } else {
                System.exit(0);
            }
        } catch (InterruptedException e) {
            log.info("发送消息时有错误发生", e);
        }
        return result.get();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
