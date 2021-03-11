package cn.drelang.live.server.rtmp.message.command.netstream;

import cn.drelang.live.server.exception.OperationNotSupportException;
import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import cn.drelang.live.util.ByteUtil;
import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * 响应客户端的 NetStream 命令
 *
 * @author Drelang
 * @date 2021/3/11 22:56
 */

public class OnStatusMessage extends CommandMessage {

    private final String commandName = "onStatus";
    private final Double transactionID = 0.0;

    /**
     * There is no command object for Object onStatus messages.
     */
    private Object commandObject;

    /**
     * An AMF object having at least the following three properties:
     *   "level" (String): the level for this message, one of "warning", "status", or "error";
     *   "code" (String): the message code, for example "NetStream.Play.Start";
     *   "description" (String): a human-readable description of the message.
     *
     *  The Info object MAY contain other properties as appropriate to the code.
     */
    private Map<String, Object> infoObject;

    public Object getCommandObject() {
        return commandObject;
    }

    public void setCommandObject(Object commandObject) {
        this.commandObject = commandObject;
    }

    public Map<String, Object> getInfoObject() {
        return infoObject;
    }

    public void setInfoObject(Map<String, Object> infoObject) {
        this.infoObject = infoObject;
    }

    @Override
    public int outboundMsid() {
        return 0x01;
    }

    @Override
    public byte outboundCsid() {
        return 0x04;
    }

    @Override
    public String toReadableString() {
        return null;
    }

    @Override
    public byte[] composeOutMessageToBytes() {
        return ByteUtil.mergeByteArray(
                AMF0.encodeAMF0Type(commandName),
                AMF0.encodeAMF0Type(transactionID),
                AMF0.encodeAMF0Type(commandObject),
                AMF0.encodeAMF0Type(infoObject));
    }

    @Override
    public void continueDecode(ByteBuf in) {
        throw new OperationNotSupportException("OnStatusMessage direction is: server to client, not support continueDecode.");
    }
}

