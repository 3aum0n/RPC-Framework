package rpc.serializer;

/**
 * @author 3aum0n
 */
public interface CommonSerializer {

    /**
     * 通用序列化接口
     *
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 通用反序列化接口
     *
     * @param bytes
     * @param clazz
     * @return
     */
    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    /**
     * 通过 code 获取对应的序列化器
     *
     * @param code
     * @return
     */
    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new HessianSerializer();
            default:
                return null;
        }
    }
}
