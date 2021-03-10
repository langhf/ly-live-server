package cn.drelang.live.server.rtmp.message.protocol;

import cn.drelang.live.util.ByteUtil;

/**
 * AbortMessage, 终止特定 ChunkStream
 *
 * @author Drelang
 * @date 2021/3/10 23:01
 */

public class AbortMessage extends ProtocolControlMessage{

    /**
     * 要终止的 chunkStreamId
     */
    private final int chunkStreamId;

    public AbortMessage(int chunkStreamId) {
        this.chunkStreamId = chunkStreamId;
    }

    @Override
    public byte outBoundMessageTypeId() {
        return 0x02;
    }

    @Override
    public byte[] outMessageToBytes() {
        return ByteUtil.convertInt2BytesBE(chunkStreamId, 4);
    }

    @Override
    public String toReadableString() {
        return "AbortMessage{" + "chunkStreamId=" + chunkStreamId + "}";
    }

    @Override
    public int outMessageLength() {
        return 4;
    }
}

