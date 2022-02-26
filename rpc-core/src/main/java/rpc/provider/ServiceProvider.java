package rpc.provider;

/**
 * 保存和提供服务实例对象
 *
 * @author 3aum0n
 */
public interface ServiceProvider {

    <T> void addServiceProvider(T service);

    Object getServiceProvider(String serviceName);
}
