package entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 3aum0n
 * @create 2022-01-07 22:55
 */
@Data
@AllArgsConstructor
public class RpcRequest implements Serializable {

    public RpcRequest() {

    }

    /*
     * 待调用接口名称
     */
    private String interfaceName;

    /*
     * 待调用方法名称
     */
    private String methodName;

    /*
     * 调用方法参数类型
     */
    private Class<?>[] paramTypes;

    /*
     * 调用方法的参数
     */
    private Object[] parameters;
}
