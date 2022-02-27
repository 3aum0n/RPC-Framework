package rpc.serializer;

/**
 * @author 3aum0n
 */
public interface CommonSerializer {

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;

    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;

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
            case 3:
                return new ProtobufSerializer();
            default:
                return null;
        }
    }

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
}
