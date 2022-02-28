package rpc.provider;

import enumeration.RpcError;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的服务注册表, 保存服务端本地服务
 *
 * @author 3aum0n
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {
    /**
     * 保证全局唯一的注册信息
     */
    private static final Map<String, Object> SERVICE_MAP = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void addServiceProvider(T service, String serviceName) {
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        SERVICE_MAP.put(serviceName, service);
        log.info("向接口:{} 注册服务:{}", service.getClass().getInterfaces(), serviceName);
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = SERVICE_MAP.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
