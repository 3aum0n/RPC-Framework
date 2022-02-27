package rpc.transport;

import entity.RpcRequest;
import rpc.serializer.CommonSerializer;

/**
 * 客户端类通用接口
 *
 * @author 3aum0n
 */
public interface RpcClient {

    int DEFAULT_SERIALIZER = CommonSerializer.HESSIAN_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);

}
