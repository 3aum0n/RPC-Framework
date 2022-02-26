package rpc.transport;

import rpc.serializer.CommonSerializer;

/**
 * 服务器类通用接口
 *
 * @author 3aum0n
 */
public interface RpcServer {

    void start();

    void setSerializer(CommonSerializer serializer);

    <T> void publishService(Object service, Class<T> serviceClass);
}