package cn.drelang.live.server.rtmp.message.protocol.userControl;

import cn.drelang.live.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

/**
 * The client sends this event to inform the server Length of the buffer size (in milliseconds)
 * that is used to buffer any data coming over a stream. This event is sent before the server
 * starts processing the stream. The first 4 bytes of the event data represent the
 * stream ID and the next 4 bytes represent the buffer length, in milliseconds.
 *
 * @author Drelang
 * @date 2021/4/9 00:21
 */

@Data
public class SetBufferLengthMessage extends UserControlMessage{

    Integer streamId;

    Integer bufferLength;

    public SetBufferLengthMessage() {
        super((short) 3);
    }

    public SetBufferLengthMessage(short type) {
        super(type);
    }

    @Override
    byte[] continueEncode() {
        ByteBuf out = Unpooled.buffer(outMessageLength());
        out.writeInt(streamId);
        out.writeInt(bufferLength);
        return ByteUtil.readAll(out);
    }

    @Override
    public String toReadableString() {
        return null;
    }

    @Override
    int additionOutMessageLength() {
        return 8;
    }
}

