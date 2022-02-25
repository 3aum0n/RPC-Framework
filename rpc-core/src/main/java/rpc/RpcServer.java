package rpc;

import rpc.serializer.CommonSerializer;

/**
 * 服务器类通用接口
 *
 * @author 3aum0n
 */
public interface RpcServer {

    void start(int port);

    void setSerializer(CommonSerializer serializer);
}
