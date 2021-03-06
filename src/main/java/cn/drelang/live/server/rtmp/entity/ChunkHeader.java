package cn.drelang.live.server.rtmp.entity;

import lombok.Data;

/**
 * RTMP 头
 *
 * @author Drelang
 * @date 2021/3/5 21:48
 */
@Data
public class ChunkHeader {

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

