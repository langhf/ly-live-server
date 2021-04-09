package cn.drelang.live.server.rtmp.message.protocol.userControl;

import cn.drelang.live.server.rtmp.message.protocol.ProtocolControlMessage;
import io.netty.buffer.ByteBuf;

/**
 * User Control Message
 *
 * @author Drelang
 * @date 2021/3/10 23:07
 */

public abstract class UserControlMessage extends ProtocolControlMessage {

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
        return 0x04;
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
}

