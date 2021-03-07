package cn.drelang.live.server.rtmp.entity;

import lombok.Data;

/**
 * 完整的一个 Rtmp 消息，包含解析后的头部信息和解析后的body数据。
 * 注意：对于音视频数据，存放的是原字节数组
 *
 * @author Drelang
 * @date 2021/3/5 22:00
 */

@Data
public class RtmpMessage {

    private RtmpHeader header;

    private RtmpBody body;

    public RtmpMessage(RtmpHeader header, RtmpBody body) {
        this.header = header;
        this.body = body;
    }
}

