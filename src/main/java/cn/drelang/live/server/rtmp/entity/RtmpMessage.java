package cn.drelang.live.server.rtmp.entity;

import lombok.Data;

/**
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

