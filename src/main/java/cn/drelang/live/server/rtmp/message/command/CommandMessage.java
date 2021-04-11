package cn.drelang.live.server.rtmp.message.command;

import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.entity.Constants;
import cn.drelang.live.server.rtmp.message.command.netconnection.ConnectMessage;
import cn.drelang.live.server.rtmp.message.command.netconnection.CreateStreamMessage;
import cn.drelang.live.server.rtmp.message.command.netstream.*;
import com.google.common.collect.Maps;
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
public abstract class CommandMessage implements RtmpCommandMessage {

    public static final byte MESSAGE_TYPE_ID = Constants.COMMAND_MESSAGE_AMF0;

    private static final Map<String, Class<? extends CommandMessage>> COMMAND_MAP;

    static {
        COMMAND_MAP = Maps.newHashMap();
        COMMAND_MAP.put("connect", ConnectMessage.class);
        COMMAND_MAP.put("play", PlayMessage.class);
        COMMAND_MAP.put("releaseStream", ReleaseStreamMessage.class);
        COMMAND_MAP.put("FCPublish", FCPublishMessage.class);
        COMMAND_MAP.put("createStream", CreateStreamMessage.class);
        COMMAND_MAP.put("publish", PublishMessage.class);
        COMMAND_MAP.put("deleteStream", DeleteStreamMessage.class);
        COMMAND_MAP.put("FCUnpublish", FCUnpublishMessage.class);
    }

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
     * Chunk Body 形式的 bytes
     */
    private byte[] chunkBodyBytes;

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

    public byte outBoundMessageTypeId() {
        return MESSAGE_TYPE_ID;
    }

    @Override
    public byte[] outMessageToBytes() {
        if (chunkBodyBytes == null) {   // 类似缓存的用法
            // TODO: commandName 和 txid 子类都要 compose 一遍，不如提取到父类统一做一下
            chunkBodyBytes = composeOutMessageToBytes();
        }
        return chunkBodyBytes;
    }

    public abstract byte[] composeOutMessageToBytes();

    /**
     * 获取完整的对象，将剩下没解析的内容解析出来。
     * 已经解析的内容指的是：commandName 和 transactionId
     * @param in 待解析的字节数据
     */
    public abstract void continueDecode(ByteBuf in);

    /**
     * 根据传入的数组，创建一个完整的 CommandMessage
     */
    public static CommandMessage createInstance(ByteBuf in) throws RuntimeException {
        CommandMessage commandMessage = null;
        String commandName = null;
        try {
            commandName = (String) AMF0.decodeAMF0Type(in);
            Class<? extends CommandMessage> command = COMMAND_MAP.get(commandName);
            if (command == null) {
                throw new RuntimeException("commandName error not support " + commandName);
            }
            commandMessage = command.newInstance();
            commandMessage.setCommandName(commandName);
            commandMessage.setTransactionID((Double) AMF0.decodeAMF0Type(in));
            commandMessage.continueDecode(in);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException("can not parse command name " + commandName, e);
        }
        return commandMessage;
    }

}

