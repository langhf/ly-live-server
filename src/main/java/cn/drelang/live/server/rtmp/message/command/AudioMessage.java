package cn.drelang.live.server.rtmp.message.command;

/**
 * 音频消息
 *
 * @author Drelang
 * @date 2021/3/7 16:45
 */

public class AudioMessage implements RtmpCommandMessage {
    @Override
    public byte outBoundMessageTypeId() {
        return 0x08;
    }

    @Override
    public byte[] messageToBytes() {
        return new byte[0];
    }

    @Override
    public String toReadableString() {
        return null;
    }
}

