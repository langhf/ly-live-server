package cn.drelang.live.server.rtmp.message.command;

import io.netty.buffer.ByteBuf;

/**
 * TODO:
 *
 * @author Drelang
 * @date 2021/3/7 16:46
 */

public class PlayMessage extends CommandMessage {

    @Override
    public byte[] composeOutMessageToBytes() {
        return new byte[0];
    }

    @Override
    public String toReadableString() {
        return null;
    }

    @Override
    public void continueDecode(ByteBuf in) {

    }
}

