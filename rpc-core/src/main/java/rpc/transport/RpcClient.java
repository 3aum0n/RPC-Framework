package rpc.transport;

import entity.RpcRequest;
import rpc.serializer.CommonSerializer;

/**
 * 客户端类通用接口
 *
 * @author 3aum0n
 */
public interface RpcClient {

    Object sendRequest(RpcRequest rpcRequest);

    void setSerializer(CommonSerializer serializer);
}
