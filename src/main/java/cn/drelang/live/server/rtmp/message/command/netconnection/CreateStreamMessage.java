package cn.drelang.live.server.rtmp.message.command.netconnection;

import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import cn.drelang.live.util.ByteUtil;
import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * 推流创建流的消息
 *
 * @author Drelang
 * @date 2021/3/7 20:42
 */

public class CreateStreamMessage extends CommandMessage {

    /**
     * 入站，命令对象
     */
    private Map<String, Object> inCommandObject;

    /**
     * 出站：命令对象
     */
    private Map<String, Object> outCommandObject;

    public Map<String, Object> getInCommandObject() {
        return inCommandObject;
    }

    public void setInCommandObject(Map<String, Object> inCommandObject) {
        this.inCommandObject = inCommandObject;
    }

    public Map<String, Object> getOutCommandObject() {
        return outCommandObject;
    }

    public void setOutCommandObject(Map<String, Object> outCommandObject) {
        this.outCommandObject = outCommandObject;
    }

    public int getOutStreamId() {
        return outStreamId;
    }

    public void setOutStreamId(int outStreamId) {
        this.outStreamId = outStreamId;
    }

    /**
     * 出站：分配的 StreamId
     */
    private int outStreamId;

    @Override
    public byte[] composeOutMessageToBytes() {
        byte[] b1 = AMF0.encodeAMF0Type(commandName);
        byte[] b2 = AMF0.encodeAMF0Type(transactionID);
        byte[] b3 = AMF0.encodeAMF0Type(outCommandObject);
        byte[] b4 = AMF0.encodeAMF0Type(outStreamId);
        return ByteUtil.mergeByteArray(b1, b2, b3, b4);
    }

    @Override
    public String toReadableString() {
        return "ReleaseStreamMessage{" +
                "commandName=" + commandName + "," +
                "txid=" + transactionID + "," +
                "mark=" + inCommandObject +
                "}";
    }

    @Override
    public void continueDecode(ByteBuf in) {
        inCommandObject = (Map) AMF0.decodeAMF0Type(in);
    }
}

