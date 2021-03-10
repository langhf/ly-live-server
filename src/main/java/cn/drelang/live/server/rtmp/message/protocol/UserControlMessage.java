package cn.drelang.live.server.rtmp.message.protocol;

import cn.drelang.live.server.exception.OperationNotSupportException;

/**
 * User Control Message
 *
 * @author Drelang
 * @date 2021/3/10 23:07
 */

public class UserControlMessage extends ProtocolControlMessage{
    @Override
    public byte outBoundMessageTypeId() {
        return 0;
    }

    @Override
    public byte[] outMessageToBytes() {
        return new byte[0];
    }

    @Override
    public String toReadableString() {
        return null;
    }

    @Override
    public int outMessageLength() {
        throw new OperationNotSupportException("this class not support this method");
    }
}

