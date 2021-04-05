package cn.drelang.live.server.rtmp.entity;

import cn.drelang.live.server.rtmp.message.media.AudioMessage;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import cn.drelang.live.server.rtmp.message.command.RtmpCommandMessage;
import cn.drelang.live.server.rtmp.message.protocol.ProtocolControlMessage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * RTMP 头
 *
 * @author Drelang
 * @date 2021/3/5 21:48
 */
@Data
public class RtmpHeader {

    /** Chunk Header */
    public static final byte TIMESTAMP_LEN = 3;
    public static final byte EXTENDED_TIMESTAMP_LEN = 4;
    public static final byte MESSAGE_LENGTH__LEN = 4;
    public static final byte MESSAGE_TYPE_LEN = 1;
    public static final byte MESSAGE_STREAM_ID_LEN = 4;

    public static final int NULL = 0xFFFFFFFF;

    //  basic header
    /**
     * message header 格式
     */
    private byte fmt;

    /**
     * 真实的 CSID
     */
    private int chunkStreamId;

    // message header
    /**
     * 时间戳
     */
    private int timeStamp;


    /**
     * 消息长度
     */
    private int messageLength;

    /**
     * 消息类型
     */
    private byte messageTypeId;

    /**
     * 消息 stream id
     */
    private int messageStreamId;

    /**
     * 原始 body 字节数组
     */
    private ByteBuf rawBodyBytes;

    /**
     * 剩余要读取的body数据
     */
    private int leftToRead;

    public RtmpHeader() {}

    public RtmpHeader(RtmpHeader source) {
        fmt = source.fmt;
        chunkStreamId = source.chunkStreamId;
        messageLength = source.messageLength;
        messageTypeId = source.messageTypeId;
        messageStreamId = source.messageStreamId;
        timeStamp = source.timeStamp;
    }

    /**
     * 根据要发送的 RtmpBody 类型，生成 RtmpHeader
     */
    public static RtmpHeader createOutHeaderByMessage(RtmpBody body) {
        RtmpHeader header = new RtmpHeader();
        if (body instanceof ProtocolControlMessage) {
            ProtocolControlMessage m = (ProtocolControlMessage) body;
            header.setFmt((byte) 0);
            header.setChunkStreamId(m.outChunkStreamId());
            header.setTimeStamp(0);
            header.setMessageLength(m.outMessageLength());
            header.setMessageTypeId(m.outBoundMessageTypeId());
            header.setMessageStreamId(m.outMessageStreamId());
        } else if (body instanceof RtmpCommandMessage) {
            if (body instanceof CommandMessage) {

            } else if (body instanceof AudioMessage) {

            }
        }
        return header;
    }
}

