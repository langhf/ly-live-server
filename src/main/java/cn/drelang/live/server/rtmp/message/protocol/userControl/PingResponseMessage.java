package cn.drelang.live.server.rtmp.message.protocol.userControl;

import cn.drelang.live.util.ByteUtil;
import lombok.Data;

/**
 * The client sends this event to the server in response to the ping request. The event data is
 * a 4-byte timestamp, which was received with the PingRequest request.
 *
 * @author Drelang
 * @date 2021/4/9 23:00
 */

@Data
public class PingResponseMessage extends UserControlMessage{

    Long timeStamp;

    public PingResponseMessage() {
        super((short) 7);
    }

    public PingResponseMessage(short type) {
        super(type);
    }

    @Override
    public byte[] outMessageToBytes() {
        return ByteUtil.convertInt2BytesBE(timeStamp.intValue(), 4);
    }

    @Override
    public String toReadableString() {
        return null;
    }

    @Override
    public int outMessageLength() {
        return 4;
    }
}

