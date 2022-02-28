package rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import enumeration.RpcError;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import rpc.loadbalancer.LoadBalancer;
import rpc.loadbalancer.RandomLoadBalancer;
import util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author 3aum0n
 */
@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery {

    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if (loadBalancer == null) {
            this.loadBalancer = new RandomLoadBalancer();
        } else {
            this.loadBalancer = loadBalancer;
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            if (instances.size() == 0) {
                log.error("找不到对应的服务" + serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("获取服务时有错误发生:", e);
        }
        return null;
    }
}
