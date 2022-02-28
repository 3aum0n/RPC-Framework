package test;

import api.HelloObject;
import api.HelloService;
import lombok.extern.slf4j.Slf4j;
import rpc.annotation.Service;

/**
 * @author 3aum0n
 */
@Slf4j
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(HelloObject object) {
        log.info("接收到消息:{}", object.getMessage());
        return "本次处理来自 Netty 服务";
    }
}
