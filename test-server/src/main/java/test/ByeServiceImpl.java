package test;

import api.ByeService;
import rpc.annotation.Service;

/**
 * @author 3aum0n
 */
@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye," + name;
    }
}
