package cn.drelang.live.server.rtmp.message.command.netstream;

import cn.drelang.live.server.exception.OperationNotSupportException;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import io.netty.buffer.ByteBuf;

/**
 * NetStream 消息，客户端发给服务端的
 *  extends by:
 *      play, play2, deleteStream, closeStream, receiveStream, receiveAudio, receiveVideo, publish, seek, pause
 *
 * @author Drelang
 * @date 2021/3/11 22:50
 */

public abstract class NetStreamC2SMessage extends CommandMessage {

    @Override
    public byte[] composeOutMessageToBytes() {
        throw new OperationNotSupportException("net stream command messages from client to server, not support server to client");
    }

}

