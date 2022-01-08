package test;

import api.HelloObject;
import api.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 3aum0n
 * @create 2022-01-07 22:44
 */
@Slf4j
public class HelloServiceImpl implements HelloService {
    //    private static final Logger logger = LoggerFactory.getLogger(test.HelloServiceImpl.class);
    @Override
    public String hello(HelloObject object) {
        log.info("接收到:{}", object.getMessage());
        return "这是调用的返回值, id=" + object.getId();
    }
}
