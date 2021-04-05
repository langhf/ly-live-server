package cn.drelang.live.server.rtmp.message.media;

import cn.drelang.live.server.rtmp.entity.RtmpBody;

/**
 *
 * @author Drelang
 * @date 2021/4/5 12:11
 */

public abstract class MediaMessage implements RtmpBody {
    byte[] rawMediaData;

    public MediaMessage() {}

    public MediaMessage(byte[] rawMediaData) {
        this.rawMediaData = rawMediaData;
    }

    @Override
    public byte[] outMessageToBytes() {
        return rawMediaData;
    }
}

