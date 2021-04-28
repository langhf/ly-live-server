package cn.drelang.live.server.rtmp.message.media;

import cn.drelang.live.server.format.flv.FLVData;
import cn.drelang.live.server.rtmp.entity.Constants;
import cn.drelang.live.server.rtmp.entity.RtmpHeader;

/**
 * 音频消息
 *
 * @author Drelang
 * @date 2021/3/7 16:45
 */

public class AudioMessage extends MediaMessage {

    public static final byte MESSAGE_TYPE_ID = Constants.AUDIO_MESSAGE;

    public AudioMessage() {}

    public AudioMessage(byte[] data) {
        super(data);
    }
    @Override
    public byte outBoundMessageTypeId() {
        return MESSAGE_TYPE_ID;
    }

    public boolean isAACHead() {
        return rawMediaData.length > 1 && rawMediaData[1] == FLVData.Audio.AACAudioData.AACPacketType.HEAD.getCode();
    }

    @Override
    public String toReadableString() {
        return null;
    }

    public RtmpHeader creatOutHeader(int timeStamp) {
        RtmpHeader header = new RtmpHeader();
        header.setChunkStreamId(4);
        header.setTimeStamp(timeStamp);
        header.setMessageTypeId(MESSAGE_TYPE_ID);
        header.setMessageLength(outMessageToBytes().length);
        header.setMessageStreamId(1);
        return header;
    }

    @Override
    public String toString() {
        return "AudioMessage("
                + "length=" + rawMediaData.length + ", "
                + "isAACHead=" + isAACHead()
                + ")";
    }
}

