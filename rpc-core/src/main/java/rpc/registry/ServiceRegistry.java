package rpc.registry;

/**
 * @author 3aum0n
 */
public interface ServiceRegistry {
    /**
     * 注册服务信息
     *
     * @param service
     * @param <T>
     */
    <T> void register(T service);

    /**
     * 获取服务信息
     */
    Object getService(String serviceName);
}
