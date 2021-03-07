package cn.drelang.live.server.exception;

/**
 *
 * @author Drelang
 * @date 2021/3/7 19:44
 */

public class ClassNotSupportException extends RuntimeException{

    public ClassNotSupportException() {
        super("this class not support, please implement in subclass!");
    }
    public ClassNotSupportException(String msg) {
        super(msg);
    }
}

