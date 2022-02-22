package rpc.registry;

/**
 * @author 3aum0n
 */
public interface ServiceRegistry {
    // 注册服务信息
    <T> void register(T service);

    // 获取服务信息
    Object getService(String serviceName);
}
