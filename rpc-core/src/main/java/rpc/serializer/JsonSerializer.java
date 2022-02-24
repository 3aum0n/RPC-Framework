package rpc.serializer;

import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.RpcRequest;
import enumeration.SerializerCode;
import exception.SerializeException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author 3aum0n
 */
@Slf4j
public class JsonSerializer implements CommonSerializer {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            Log.error("反序列化时有错误发生", e);
            throw new SerializeException("反序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RpcRequest) {
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            Log.error("反序列化时有错误发生", e);
            throw new SerializeException("反序列化时有错误发生");
        }
    }

    /**
     * 为了确保反序列化后仍然为原实例类型，需要重新判断
     *
     * @param obj 参与序列化、反序列化的数组
     * @return
     * @throws IOException
     */
    private Object handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) obj;
        for (int i = 0; i < rpcRequest.getParamTypes().length; i++) {
            Class<?> clazz = rpcRequest.getParamTypes()[i];
            if (!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes, clazz);
            }
        }
        return rpcRequest;
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }
}
