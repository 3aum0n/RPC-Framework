package entity;

import enumeration.ResponseCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 3aum0n
 */
@Data
public class RpcResponse<T> implements Serializable {
    /**
     * 响应状态码
     */
    private Integer statusCode;

    /**
     * 响应状态补充信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public RpcResponse() {
    }

    /**
     * 用于快速生成成功的响应对象
     * 第一个T表示<T>是一个泛型
     * 第二个T表示方法返回的是RpcResponse<T>类型的数据,其中RpcResponse中存的是T类型的数据，取出时默认为T类型
     * 第三个T表示传入的数据是T类型的
     */
    public static <T> RpcResponse<T> success(T data) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    /**
     * 用于快速生成失败的响应对象
     */
    public static <T> RpcResponse<T> fail(ResponseCode code) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }

}
