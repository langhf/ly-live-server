package cn.drelang.live.util;

import cn.drelang.live.server.http.BaseResponse;
import cn.drelang.live.server.http.RetCodeEnum;

/**
 *
 * @author Drelang
 * @date 2021/3/4 23:39
 */

public class ResponseUtil {

    public static  <T> BaseResponse<T> build(RetCodeEnum retCodeEnum, T t) {
        return new BaseResponse<>(retCodeEnum.getCode(), retCodeEnum.getMsg(), t);
    }

    public static  BaseResponse build(RetCodeEnum retCodeEnum) {
        return new BaseResponse<>(retCodeEnum.getCode(), retCodeEnum.getMsg(), null);
    }

    public static  <T> BaseResponse<T> build(int code, String msg, T t) {
        return new BaseResponse<>(code, msg, t);
    }

    public static BaseResponse<Void> build(int code, String msg) {
        return build(code, msg, null);
    }
}

