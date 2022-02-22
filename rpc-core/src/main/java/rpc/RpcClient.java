package rpc;

import entity.RpcRequest;

/**
 * 客户端类通用接口
 *
 * @author 3aum0n
 */
public interface RpcClient {

    Object sendRequest(RpcRequest rpcRequest);
}
