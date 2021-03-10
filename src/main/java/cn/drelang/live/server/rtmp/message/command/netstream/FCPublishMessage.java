package cn.drelang.live.server.rtmp.message.command.netstream;

import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import cn.drelang.live.util.ByteUtil;
import io.netty.buffer.ByteBuf;

/**
 * 推流消息，与 ReleaseStream 消息类似，同时出现的话，处理一个就行
 *
 * @author Drelang
 * @date 2021/3/10 23:26
 */

public class FCPublishMessage extends CommandMessage {
    /**
     * 入栈方向：一个 null 占位
     */
    private Object mark;

    /**
     * 入栈方向：推流密钥
     */
    private String channelKey;

    @Override
    public byte[] composeOutMessageToBytes() {
        byte[] b1 = AMF0.encodeAMF0Type(commandName);
        byte[] b2 = AMF0.encodeAMF0Type(transactionID);
        byte[] b3 = AMF0.encodeAMF0Type(mark);
        byte[] b4 = AMF0.encodeAMF0Type(channelKey);
        return ByteUtil.mergeByteArray(b1, b2, b3, b4);
    }

    @Override
    public String toReadableString() {
        return "ReleaseStreamMessage{" +
                "commandName=" + commandName + "," +
                "txid=" + transactionID + "," +
                "mark=" + mark + "," +
                "channelKey=" + channelKey + "," +
                "}";
    }

    @Override
    public void continueDecode(ByteBuf in) {
        mark = AMF0.decodeAMF0Type(in);
        channelKey = (String) AMF0.decodeAMF0Type(in);
    }
}

