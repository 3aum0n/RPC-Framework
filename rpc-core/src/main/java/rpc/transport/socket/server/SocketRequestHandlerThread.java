package rpc.transport.socket.server;

import entity.RpcRequest;
import entity.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import rpc.registry.ServiceRegistry;
import rpc.handler.RequestHandler;
import rpc.serializer.CommonSerializer;
import rpc.transport.socket.util.ObjectReader;
import rpc.transport.socket.util.ObjectWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 处理 RpcRequest 的工作线程
 *
 * @author 3aum0n
 */
@Slf4j
public class SocketRequestHandlerThread implements Runnable {
    private Socket socket;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;

    public SocketRequestHandlerThread(Socket socket, RequestHandler requestHandler, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            Object result = requestHandler.handle(rpcRequest);
            RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, response, serializer);
        } catch (IOException e) {
            log.error("调用或发送时有错误发生", e);
        }
    }
}
