package cn.drelang.live.server.rtmp.message.command;

import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.amf.ECMAArray;
import cn.drelang.live.server.rtmp.entity.Constants;
import cn.drelang.live.server.rtmp.entity.RtmpHeader;
import cn.drelang.live.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

import java.util.Map;

/**
 * 原数据消息
 *
 * @author Drelang
 * @date 2021/3/12 00:23
 */

public class DataMessage implements RtmpCommandMessage {

    public static final byte MESSAGE_TYPE_ID = Constants.METADATA_AMF0;

    private String commandName;

    private String desc;

    private ECMAArray ecmaArray;

    /**
     * 用作缓存
     */
    private byte[] outMsgBytes;

    /**
     * 是否是推流用的 DataMessage
     * 推流用的 DataMessage 才需要 commandName
     * 拉流时服务端发给客户端不包含 commandName
     */
    private boolean publish;

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ECMAArray getEcmaArray() {
        return ecmaArray;
    }

    public void setEcmaArray(ECMAArray ecmaArray) {
        this.ecmaArray = ecmaArray;
    }

    @Override
    public byte outBoundMessageTypeId() {
        return MESSAGE_TYPE_ID;
    }

    @Override
    public byte[] outMessageToBytes() {
        if (null == outMsgBytes) {
            ByteBuf buf = Unpooled.buffer();
            if (publish) {
                buf.writeBytes(AMF0.encodeAMF0Type(commandName));
            }
            buf.writeBytes(AMF0.encodeAMF0Type(desc));
            buf.writeBytes(AMF0.encodeAMF0Type(ecmaArray));
            outMsgBytes = ByteUtil.readAll(buf);
        }
        return outMsgBytes;
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

    /**
     * 从客户端的推流请求中新建一个 DataMessage
     * @param in 客户端发送的骑牛
     * @return DataMessage
     */
    public static DataMessage createInstance(ByteBuf in) {
        DataMessage dataMessage = new DataMessage();
        dataMessage.commandName = (String) AMF0.decodeAMF0Type(in);
        dataMessage.desc = (String) AMF0.decodeAMF0Type(in);
        dataMessage.ecmaArray = (ECMAArray) AMF0.decodeAMF0Type(in);
        dataMessage.publish = true;
        return dataMessage;
    }

    public static RtmpHeader createOutHeader(DataMessage dataMessage) {
        RtmpHeader header = new RtmpHeader();
        header.setChunkStreamId(6);
        header.setTimeStamp(0);
        header.setMessageStreamId(1);
        header.setMessageLength(dataMessage.outMessageToBytes().length);
        header.setMessageTypeId(MESSAGE_TYPE_ID);
        return header;
    }
}

