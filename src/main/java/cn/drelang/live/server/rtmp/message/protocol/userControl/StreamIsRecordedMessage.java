package cn.drelang.live.server.rtmp.message.protocol.userControl;

import cn.drelang.live.util.ByteUtil;

/**
 * The server sends this event to notify the client that the stream is a recorded stream. The 4 bytes event data
 * represent the stream ID of the recorded stream.
 *
 * @author Drelang
 * @date 2021/4/9 22:49
 */

public class StreamIsRecordedMessage extends UserControlMessage{

    Integer streamId;

    public StreamIsRecordedMessage() {
        super((short) 4);
    }

    public StreamIsRecordedMessage(short type) {
        super(type);
    }

    @Override
    public byte[] outMessageToBytes() {
        return ByteUtil.convertInt2BytesBE(streamId, 4);
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

