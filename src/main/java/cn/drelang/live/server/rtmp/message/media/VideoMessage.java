package cn.drelang.live.server.rtmp.message.media;

import lombok.Data;

/**
 * 视频消息
 *
 * @author Drelang
 * @date 2021/3/7 16:44
 */
@Data
public class VideoMessage extends MediaMessage {

    public VideoMessage() {}

    public VideoMessage(byte[] data) {
        super(data);
    }

    /**
     * this video frame is key frame or not.
     */
    private boolean keyFrame;

    @Override
    public byte outBoundMessageTypeId() {
        return 0x09;
    }

    @Override
    public String toReadableString() {
        return null;
    }

}

