package cn.drelang.live.server.rtmp.message.media;

import cn.drelang.live.server.format.flv.FLVData;
import cn.drelang.live.server.rtmp.entity.Constants;
import cn.drelang.live.server.rtmp.entity.RtmpHeader;

/**
 * 视频消息
 *
 * @author Drelang
 * @date 2021/3/7 16:44
 */
public class VideoMessage extends MediaMessage {

    public static final byte MESSAGE_TYPE_ID = Constants.VIDEO_MESSAGE;

    public VideoMessage() {}

    public VideoMessage(byte[] data) {
        super(data);
    }

    public boolean isKeyFrame() {
        return FLVData.Video.FRAME_TYPE.KEY_FRAME.getCode() == ((rawMediaData[0] & 0xF0) >> 4);
    }

    public boolean isKeyFrameHead() {
        return isKeyFrame() && rawMediaData.length > 1 && rawMediaData[1] == FLVData.Video.AVCVideoPacket.AVCPacketType.HEAD.getCode();
    }

    @Override
    public byte outBoundMessageTypeId() {
        return MESSAGE_TYPE_ID;
    }

    @Override
    public String toReadableString() {
        return null;
    }

    public RtmpHeader creatOutHeader(int timeStamp) {
        RtmpHeader header = new RtmpHeader();
        header.setChunkStreamId(6);
        header.setTimeStamp(timeStamp);
        header.setMessageTypeId(MESSAGE_TYPE_ID);
        header.setMessageLength(outMessageToBytes().length);
        header.setMessageStreamId(1);
        return header;
    }

    @Override
    public String toString() {
        return "VideoMessage("
                + "length=" + rawMediaData.length + ", "
                + "isKeyFrame=" + isKeyFrame() + ", "
                + "isKeyFrameHead=" + isKeyFrameHead()
                + ")";
    }
}

