package rpc.transport.netty.client;

import entity.RpcRequest;
import entity.RpcResponse;
import factory.SingletonFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import rpc.serializer.CommonSerializer;

import java.net.InetSocketAddress;

/**
 * Netty 客户端侧处理器
 * 处理收到的消息，即 RpcResponse 对象，将返回的结果放入 ctx 中
 *
 * @author 3aum0n
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private final UnprocessedRequests unprocessedRequests;

    public NettyClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try {
            log.info(String.format("客户端接收到消息: %s", msg));
            unprocessedRequests.complete(msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleState) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("发送心跳包♥[{}]", ctx.channel().remoteAddress());
                Channel channel = ChannelProvider.get((InetSocketAddress) ctx.channel().remoteAddress(),
                        CommonSerializer.getByCode(CommonSerializer.DEFAULT_SERIALIZER));
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setHeartBeat(true);
                channel.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("过程调用时有错误发生");
        cause.printStackTrace();
        ctx.close();
    }
}
