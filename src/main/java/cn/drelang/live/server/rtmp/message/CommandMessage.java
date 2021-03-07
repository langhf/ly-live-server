package cn.drelang.live.server.rtmp.message;

import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.entity.RtmpBody;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

import java.util.Map;

/**
 * 协议原文：
 *   A command message consists of command name, transaction ID, and command object that contains related parameters.
 * @author Drelang
 * @date 2021/3/6 16:37
 */

@Data
public class CommandMessage extends RtmpBody {

    /**
     * 命令名称
     */
    private String commandName;

    /**
     * 事务ID
     */
    private Double transactionID;

    private Map<String, Object> properties;

    /**
     * 出站 channel stream id，协议规定为 2
     */
    public byte outboundCsid() {
        return 0x02;
    }

    /**
     * 出站 message stream id
     */
    public int outboundMsid() {
        return 0;
    }

    /**
     * 根据字节数组生成一个 CommandMessage
     */
    public static CommandMessage decode4AMF0(ByteBuf in) {
        CommandMessage commandMessage  = new CommandMessage();
        commandMessage.setCommandName((String) AMF0.decodeAMF0Type(in));
        commandMessage.setTransactionID((Double) AMF0.decodeAMF0Type(in));
        commandMessage.setProperties((Map) AMF0.decodeAMF0Type(in));
        return commandMessage;
    }

    /**
     * 将一个 CommandMessage 序列化成 AMF0 字节数组
     */
    public static ByteBuf encode4AMF0(CommandMessage in) {
        ByteBuf out = Unpooled.buffer();
        out.writeBytes(AMF0.encodeAMF0Type(in.getCommandName()));
        out.writeBytes(AMF0.encodeAMF0Type(in.getTransactionID()));
        out.writeBytes(AMF0.encodeAMF0Type(in.getProperties()));
        return out;
    }

}

