package cn.drelang.live.server.rtmp.message.protocol;

import cn.drelang.live.server.rtmp.entity.RtmpBody;

/**
 * 协议控制消息，括号内为 Message Type Id，包含：
 *  - Set Chunk Size (1)
 *  - Abort Message (2)
 *  - Acknowledgement (3)
 *  - Window Acknowledgement Size (5)
 *  - Set Peer Bandwidth (6)
 *
 * 协议原文对于 Protocol Control Message 的说明：
 *   These protocol control messages MUST have message stream ID 0 (known as the control stream) and be sent in chunk
 *   stream ID 2. Protocol control messages take effect as soon as they are received; their timestamps are ignored.
 *
 * @author Drelang
 * @date 2021/3/7 17:59
 */

public abstract class ProtocolControlMessage implements RtmpBody {

    public byte outChunkStreamId() {
        return 0x02;
    }

    public byte outMessageStreamId() {
        return 0x00;
    }

    public abstract byte outMessageTypeId();

    public abstract int outMessageLength();

    public abstract byte[] messageToBytes();

    @Override
    public String toString() {
        return this.getClass().getName() + "{" +
                "csid=" + outChunkStreamId() + ", " +
                "msid=" + outMessageStreamId() + ", " +
                "typeId=" + outMessageTypeId() + ", " +
                "}";
    }

}

