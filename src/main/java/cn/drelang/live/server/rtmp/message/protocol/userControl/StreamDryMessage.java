package cn.drelang.live.server.rtmp.message.protocol.userControl;

import cn.drelang.live.util.ByteUtil;
import lombok.Data;

/**
 * The server sends this event to notify the client that there is no more data on the stream. If the
 * server does not detect any message for a time period, it can notify the subscribed clients
 * that the stream is dry. The 4 bytes of event data represent the stream ID of the dry stream.
 *
 * @author Drelang
 * @date 2021/4/9 00:17
 */

@Data
public class StreamDryMessage extends UserControlMessage{

    Integer streamId;

    public StreamDryMessage() {
        super((short) 2);
    }

    public StreamDryMessage(short type) {
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

