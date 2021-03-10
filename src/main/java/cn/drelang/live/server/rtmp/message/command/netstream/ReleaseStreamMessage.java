package cn.drelang.live.server.rtmp.message.command.netstream;

import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import cn.drelang.live.util.ByteUtil;
import io.netty.buffer.ByteBuf;

/**
 * 推流发送密钥
 *
 * @author Drelang
 * @date 2021/3/10 23:15
 */

public class ReleaseStreamMessage extends CommandMessage {

    /**
     * 入栈方向：一个 null 占位
     */
    private Object mark;

    /**
     * 入栈方向：推流密钥
     */
    private String channelKey;

    public Object getMark() {
        return mark;
    }

    public void setMark(Object mark) {
        this.mark = mark;
    }

    public String getChannelKey() {
        return channelKey;
    }

    public void setChannelKey(String channelKey) {
        this.channelKey = channelKey;
    }

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

