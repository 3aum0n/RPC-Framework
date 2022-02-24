package rpc;

import entity.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import rpc.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * RPC客户端动态代理
 *
 * @author 3aum0n
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    /*
     * 生成代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /*
     * 指明代理对象的方法被调用时的动作
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("调用方法:{}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(),
                method.getName(), method.getParameterTypes(), args);
        return client.sendRequest(rpcRequest);
    }
}
