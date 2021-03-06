package cn.drelang.live.server.rtmp.entity;

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
    private int channelStreamId;

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

}

