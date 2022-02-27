package rpc.transport.netty.server;

import entity.RpcRequest;
import entity.RpcResponse;
import factory.SingletonFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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

    private static final String THREAD_NAME_PREFIX = "netty-server-handler";
    private RequestHandler requestHandler;
    private final ExecutorService threadPool;

    public NettyServerHandler() {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
        this.threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        threadPool.execute(() -> {
            try {
                log.info("服务器接收到请求:{}", msg);
                Object result = requestHandler.handle(msg);
                ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result, msg.getRequestId()));
                future.addListener(ChannelFutureListener.CLOSE);
            } finally {
                ReferenceCountUtil.release(msg);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理过程调用时有错误发生");
        cause.printStackTrace();
        ctx.close();
    }
}
