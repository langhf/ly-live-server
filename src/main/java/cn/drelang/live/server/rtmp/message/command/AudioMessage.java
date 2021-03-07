package cn.drelang.live.server.rtmp.message.command;

import cn.drelang.live.server.rtmp.entity.RtmpBody;

/**
 * 音频消息
 *
 * @author Drelang
 * @date 2021/3/7 16:45
 */

public class AudioMessage extends RtmpCommandMessage {
    @Override
    public byte[] messageToBytes() {
        return new byte[0];
    }

    @Override
    public byte outMessageTypeId() {
        return 0;
    }
}

