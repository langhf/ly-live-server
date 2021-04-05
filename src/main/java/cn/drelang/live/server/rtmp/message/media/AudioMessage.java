package cn.drelang.live.server.rtmp.message.media;

/**
 * 音频消息
 *
 * @author Drelang
 * @date 2021/3/7 16:45
 */

public class AudioMessage extends MediaMessage {

    public AudioMessage() {}

    public AudioMessage(byte[] data) {
        super(data);
    }
    @Override
    public byte outBoundMessageTypeId() {
        return 0x08;
    }

    @Override
    public String toReadableString() {
        return null;
    }
}

