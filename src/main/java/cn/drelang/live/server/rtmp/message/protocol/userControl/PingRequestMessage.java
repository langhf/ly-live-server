package cn.drelang.live.server.rtmp.message.protocol.userControl;

import cn.drelang.live.util.ByteUtil;
import lombok.Data;

/**
 * The server sends this event to test whether the client is reachable. Event data is a 4-byte
 * timestamp, representing the local server time when the server dispatched the command. The
 * client responds with PingResponse on receiving MsgPingRequest.
 *
 * @author Drelang
 * @date 2021/4/9 22:52
 */

@Data
public class PingRequestMessage extends UserControlMessage{

    Long timeStamp;

    public PingRequestMessage() {
        super((short) 6);
    }

    public PingRequestMessage(short type) {
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

