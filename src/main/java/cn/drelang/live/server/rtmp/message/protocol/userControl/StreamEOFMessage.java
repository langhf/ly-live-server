package cn.drelang.live.server.rtmp.message.protocol.userControl;

import cn.drelang.live.util.ByteUtil;
import lombok.Data;

/**
 * The server sends this event to notify the client that the playback of data is over as requested
 *  on this stream. No more data is sent without issuing additional commands. The client discards
 *  the messages received for the stream. The 4 bytes of event data represent the ID of the
 *  stream on which playback has ended.
 *
 * @author Drelang
 * @date 2021/4/9 00:07
 */

@Data
public class StreamEOFMessage extends UserControlMessage{

    Integer streamId;

    public StreamEOFMessage() {
        super((short) 1);
    }

    public StreamEOFMessage(short type) {
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

