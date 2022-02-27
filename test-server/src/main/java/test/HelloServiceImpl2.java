package test;

import api.HelloObject;
import api.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 3aum0n
 */
@Slf4j
public class HelloServiceImpl2 implements HelloService {
    @Override
    public String hello(HelloObject object) {
        log.info("接收到消息:{}", object.getMessage());
        return "本次处理来自 Socket 服务";
    }
}
