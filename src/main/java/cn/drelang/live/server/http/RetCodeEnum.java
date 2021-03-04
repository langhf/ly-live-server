package cn.drelang.live.server.http;

/**
 * TODO:
 *
 * @author Drelang
 * @date 2021/3/4 23:38
 */

public enum RetCodeEnum {
    OK(0, "成功"),
    NOT_FOUNT(404, "uri有误")
            ;
    private final int code;

    private final String msg;

    RetCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}

