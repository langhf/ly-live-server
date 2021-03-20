package cn.drelang.live.server.rtmp.message.command;

import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.amf.ECMAArray;
import cn.drelang.live.server.rtmp.entity.Constants;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.Map;

/**
 * 原数据消息
 *
 * @author Drelang
 * @date 2021/3/12 00:23
 */

@Data
public class DataMessage implements RtmpCommandMessage {

    private String commandName;

    private String desc;

    private ECMAArray ecmaArray;


    @Override
    public byte outBoundMessageTypeId() {
        return Constants.METADATA_AMF0;
    }

    @Override
    public byte[] outMessageToBytes() {
        return new byte[0];
    }

    @Override
    public String toReadableString() {
        return null;
    }

    public int outMessageStreamId() {
        return 0x01;
    }

    public int outChunkStreamId() {
        return 0x04;
    }

    public static DataMessage createInstance(ByteBuf in) {
        DataMessage dataMessage = new DataMessage();
        dataMessage.setCommandName((String) AMF0.decodeAMF0Type(in));
        dataMessage.setDesc((String) AMF0.decodeAMF0Type(in));
        dataMessage.setEcmaArray((ECMAArray) AMF0.decodeAMF0Type(in));
        return dataMessage;
    }
}

