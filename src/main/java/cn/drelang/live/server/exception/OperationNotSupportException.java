package cn.drelang.live.server.exception;

/**
 *
 * @author Drelang
 * @date 2021/3/7 19:44
 */

public class OperationNotSupportException extends RuntimeException{

    public OperationNotSupportException() {
        super("this class not support, please implement in subclass!");
    }
    public OperationNotSupportException(String msg) {
        super(msg);
    }
}

