package cn.drelang.live.server.rtmp.message.command;

/**
 * 视频消息
 *
 * @author Drelang
 * @date 2021/3/7 16:44
 */

public class VideoMessage extends RtmpCommandMessage {
    @Override
    public byte[] messageToBytes() {
        return new byte[0];
    }

    @Override
    public byte outMessageTypeId() {
        return 0;
    }
}

