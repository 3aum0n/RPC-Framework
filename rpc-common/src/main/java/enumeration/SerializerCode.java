package enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 3aum0n
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {

    JSON(1);

    private final int code;
}
