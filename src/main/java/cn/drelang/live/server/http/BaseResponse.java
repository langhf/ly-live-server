package cn.drelang.live.server.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

/**
 *
 * @author Drelang
 * @date 2021/3/4 23:35
 */
@Data
public class BaseResponse<T> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private int code;

    private String msg;

    private T data;

    public BaseResponse(int code, String msg, T t) {
        this.code = code;
        this.msg = msg;
        this.data = t;
    }

    public String toJsonString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

