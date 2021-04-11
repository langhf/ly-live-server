package cn.drelang.live.server.rtmp.message.protocol.userControl;

import cn.drelang.live.server.rtmp.entity.RtmpHeader;
import cn.drelang.live.server.rtmp.message.protocol.ProtocolControlMessage;
import cn.drelang.live.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * User Control Message
 *
 * @author Drelang
 * @date 2021/3/10 23:07
 */

public abstract class UserControlMessage extends ProtocolControlMessage {

    public static final byte MESSAGE_TYPE_ID = 0x04;

    /**
     * +------------------------------+-------------------------
     * | Event Type (16 bits) | Event Data
     * +------------------------------+-------------------------
     */
    Short eventType;

    public Short getEventType() {
        return eventType;
    }

    UserControlMessage() {}

    UserControlMessage(short type) {
        this.eventType = type;
    }

    @Override
    public byte outBoundMessageTypeId() {
        return MESSAGE_TYPE_ID;
    }

    public static UserControlMessage createInstanceByType(short type, ByteBuf in) {
        UserControlMessage userControlMessage;
        if (type == 0) {
            userControlMessage = new StreamBeginMessage(type);
            ((StreamBeginMessage) userControlMessage).streamId = in.readInt();
        } else if (type == 1) {
            userControlMessage = new StreamEOFMessage(type);
            ((StreamEOFMessage) userControlMessage).streamId = in.readInt();
        } else if (type == 2) {
            userControlMessage = new StreamDryMessage(type);
            ((StreamDryMessage) userControlMessage).streamId = in.readInt();
        } else if (type == 3) {
            userControlMessage = new SetBufferLengthMessage(type);
            ((SetBufferLengthMessage) userControlMessage).streamId = in.readInt();
            ((SetBufferLengthMessage) userControlMessage).bufferLength = in.readInt();
        } else if (type == 4) {
            userControlMessage = new StreamIsRecordedMessage(type);
            ((StreamIsRecordedMessage) userControlMessage).streamId = in.readInt();
        } else if (type == 6) {
            userControlMessage = new PingRequestMessage(type);
            ((PingRequestMessage) userControlMessage).timeStamp = in.readUnsignedInt();
        } else if (type == 7) {
            userControlMessage = new PingResponseMessage(type);
            ((PingResponseMessage) userControlMessage).timeStamp = in.readUnsignedInt();
        } else {
            throw new RuntimeException("type " + type + " not support!");
        }
        return userControlMessage;
    }

    public static RtmpHeader createOutHeader(UserControlMessage message) {
        RtmpHeader header = new RtmpHeader();
        header.setChunkStreamId(2);
        header.setTimeStamp(0);
        header.setMessageLength(message.outMessageLength());
        header.setMessageTypeId(MESSAGE_TYPE_ID);
        header.setMessageStreamId(OUT_MESSAGE_STREAM_ID);
        return header;
    }

    @Override
    public byte[] outMessageToBytes() {
        ByteBuf out = Unpooled.buffer();
        out.writeShort(eventType);
        out.writeBytes(continueEncode());
        return ByteUtil.readAll(out);
    }

    abstract byte[] continueEncode();

    @Override
    public int outMessageLength() {
        // eventType length = 2 byte
        return 2 + additionOutMessageLength();
    }

    abstract int additionOutMessageLength();
}

