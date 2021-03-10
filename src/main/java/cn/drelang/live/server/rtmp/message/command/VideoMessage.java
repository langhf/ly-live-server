package cn.drelang.live.server.rtmp.message.command;

/**
 * 视频消息
 *
 * @author Drelang
 * @date 2021/3/7 16:44
 */

public class VideoMessage implements RtmpCommandMessage {
    @Override
    public byte outBoundMessageTypeId() {
        return 0x09;
    }

    @Override
    public byte[] outMessageToBytes() {
        return new byte[0];
    }

    @Override
    public String toReadableString() {
        return null;
    }

}

