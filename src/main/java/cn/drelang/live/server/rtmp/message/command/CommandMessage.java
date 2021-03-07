package cn.drelang.live.server.rtmp.message.command;

import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.entity.Constants;
import cn.drelang.live.server.rtmp.message.command.netconnection.ConnectMessage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.Map;

/**
 * 协议原文：
 *   A command message consists of command name, transaction ID, and command object that contains related parameters.
 *
 * 协议原文：
 *  The sender sends a command message that consists of command name,
 * transaction ID, and command object that contains related parameters.
 *
 * @author Drelang
 * @date 2021/3/6 16:37
 */

@Data
public class CommandMessage extends RtmpCommandMessage {

    /**
     * 命令名称， 进出站都有
     * 进站时：
     *
     *
     * 出站时：
     *  _result or _error; indicates whether the response is result or error.
     */
    protected String commandName;

    /**
     * 事务ID， 进出站都有
     *
     * 出站时：
     *
     */
    protected Double transactionID;

    /**
     * 出站 channel stream id，协议并没有规定，一般用 3
     */
    public byte outboundCsid() {
        return 0x03;
    }

    /**
     * 出站 message stream id
     */
    public int outboundMsid() {
        return 0;
    }

    public static CommandMessage decodeCMDNameAndTXID4AMF0(ByteBuf in) {
        CommandMessage com  = new CommandMessage();
        com.setCommandName((String) AMF0.decodeAMF0Type(in));
        com.setTransactionID((Double) AMF0.decodeAMF0Type(in));
        return com;
    }

    public <T extends CommandMessage> T outputSub(Class<T> type) throws IllegalAccessException, InstantiationException {
        T t = type.newInstance();
        t.setCommandName(commandName);
        t.setTransactionID(transactionID);
        return t;
    }

    @Override
    public byte outMessageTypeId() {
        return Constants.COMMAND_MESSAGE_AMF0;
    }

    @Override
    public byte[] messageToBytes() {
        return new byte[0];
    }
}

