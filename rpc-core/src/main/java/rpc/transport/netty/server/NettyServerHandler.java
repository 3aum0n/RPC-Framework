package rpc.transport.netty.server;

import entity.RpcRequest;
import entity.RpcResponse;
import factory.SingletonFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import rpc.handler.RequestHandler;
import factory.ThreadPoolFactory;

import java.util.concurrent.ExecutorService;

/**
 * 接收 RpcRequest，并且执行调用，将调用结果返回封装成 RpcResponse 发送出去
 *
 * @author 3aum0n
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private RequestHandler requestHandler;

    public NettyServerHandler() {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try {
            if (msg.getHeartBeat()) {
                log.info("接收到客户端心跳包♥...");
                return;
            }
            log.info("服务器接收到请求:{}", msg);
            Object result = requestHandler.handle(msg);
            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result, msg.getRequestId()));
            future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleState) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("长时间未收到心跳包，断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理过程调用时有错误发生");
        cause.printStackTrace();
        ctx.close();
    }
}
