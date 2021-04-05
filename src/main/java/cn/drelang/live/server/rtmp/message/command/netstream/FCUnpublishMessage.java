package cn.drelang.live.server.rtmp.message.command.netstream;

import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 *
 * @author Drelang
 * @date 2021/3/11 22:22
 */

@Data
public class FCUnpublishMessage extends CommandMessage {

    private Object mark;

    private String channelKey;

    @Override
    public String toReadableString() {
        return null;
    }

    @Override
    public byte[] composeOutMessageToBytes() {
        return new byte[0];
    }

    @Override
    public void continueDecode(ByteBuf in) {
        mark = AMF0.decodeAMF0Type(in);
        channelKey = (String) AMF0.decodeAMF0Type(in);
    }
}

