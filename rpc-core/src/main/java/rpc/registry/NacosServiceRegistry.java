package rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.esotericsoftware.minlog.Log;
import enumeration.RpcError;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Nacos 服务注册中心
 *
 * @author 3aum0n
 */
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry {

    private final NamingService namingService;

    public NacosServiceRegistry() {
        namingService = NacosUtil.getNacosNamingService();
    }

    @Override
    public <T> void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(namingService, serviceName, inetSocketAddress);
        } catch (NacosException e) {
            log.error("注册服务时有错误发生", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

}
