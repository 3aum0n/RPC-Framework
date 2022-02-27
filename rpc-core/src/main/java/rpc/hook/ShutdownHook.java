package rpc.hook;

import factory.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;
import util.NacosUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * @author 3aum0n
 */
@Slf4j
public class ShutdownHook {

    private static final String THREAD_NAME_PREFIX = "shutdown-hook";
    private static final ShutdownHook SHUTDOWN_HOOK = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return SHUTDOWN_HOOK;
    }

    public void addClearAllHook() {
        log.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
