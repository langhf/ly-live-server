package cn.drelang.live.server.rtmp.message.command.netstream;

import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import io.netty.buffer.ByteBuf;

/**
 * TODO:
 *
 * @author Drelang
 * @date 2021/3/11 22:22
 */

public class DeleteStreamMessage extends CommandMessage {

    /**
     * 入站防线，null 占位
     */
    private Object mark;

    /**
     * 入站 msid
     */
    private Double messageStreamId;

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
        messageStreamId = (Double) AMF0.decodeAMF0Type(in);
    }
}

