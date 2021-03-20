package cn.drelang.live.server.rtmp.message.command.netconnection;

import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import cn.drelang.live.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

import java.util.Map;

/**
 *
 * @author Drelang
 * @date 2021/3/7 20:42
 */

@Data
public class ConnectMessage extends CommandMessage {

    /**
     * 命令信息，进站才有.
     * 协议原文：Command information object which has the name-value pairs.
     */
    private Map<String, Object> commandObject;

    /**
     * 用户可选参数，进站才有.
     * 协议原文：Any optional information
     */
    private Map<String, Object> optionalUserArguments;

    /**
     * 一些属性，出站才有。
     * 协议原文： Name-value pairs that describe the properties(fmsver etc.) of the connection
     */
    private Map<String, Object> properties;

    /**
     * 一些信息，出站才有
     * 协议原文：Name-value pairs that describe the response from the server. ’code’, ’level’, ’description’ are names
     *          of few among such information.
     */
    private Map<String, Object> information;

    /**
     * 原始字节数组
     */
    private byte[] rawBytes;

    public ConnectMessage() {}

    /**
     * 根据字节数组生成一个 CommandMessage
     */
    public ConnectMessage decodeArguments4AMF0(ByteBuf in) {
        // 需要父类先读取命令名和事务id后才能调用此方法
        assert commandName != null && transactionID != null;
        commandObject = (Map) AMF0.decodeAMF0Type(in);
        optionalUserArguments = (Map) AMF0.decodeAMF0Type(in);
        return this;
    }

    /**
     * 将一个 CommandMessage 序列化成 AMF0 字节数组
     */
    public static ByteBuf encode4AMF0(CommandMessage in) {
        ByteBuf out = Unpooled.buffer();
        out.writeBytes(AMF0.encodeAMF0Type(in.getCommandName()));
        out.writeBytes(AMF0.encodeAMF0Type(in.getTransactionID()));
//        out.writeBytes(AMF0.encodeAMF0Type(in.getProperties()));
        return out;
    }

    @Override
    public byte[] composeOutMessageToBytes() {
        byte[] b1 = AMF0.encodeAMF0Type(commandName);
        byte[] b2 = AMF0.encodeAMF0Type(transactionID);
        byte[] b3 = AMF0.encodeAMF0Type(properties);
        byte[] b4 = AMF0.encodeAMF0Type(information);
        return ByteUtil.mergeByteArray(b1, b2, b3, b4);
    }

    @Override
    public String toReadableString() {
        return null;
    }

    @Override
    public void continueDecode(ByteBuf in) {
        commandObject = (Map) AMF0.decodeAMF0Type(in);
//        optionalUserArguments = (Map) AMF0.decodeAMF0Type(in);
    }
}

