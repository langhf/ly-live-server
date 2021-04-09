package cn.drelang.live.server.rtmp.message.protocol.userControl;

import cn.drelang.live.util.ByteUtil;
import lombok.Data;

/**
 * The server sends this event to notify the client that a stream has become functional and can be
 * used for communication. By default, this event is sent on ID 0 after the application connect
 * command is successfully received from the client. The event data is 4-byte and represents
 * the stream ID of the stream that became functional.
 *
 * @author Drelang
 * @date 2021/4/8 23:57
 */

@Data
public class StreamBeginMessage extends UserControlMessage{

    Integer streamId;

    public StreamBeginMessage() {
        super((short) 0);
    }

    public StreamBeginMessage(short type) {
        super(type);
    }

    @Override
    public byte[] outMessageToBytes() {
        return ByteUtil.convertInt2BytesBE(streamId, 4);
    }

    @Override
    public String toReadableString() {
        return "StreamBeginMessage(streamId=)" + streamId;
    }

    @Override
    public int outMessageLength() {
        return 4;
    }
}

