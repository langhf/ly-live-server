package cn.drelang.live.server.rtmp.message.protocol;

import cn.drelang.live.util.ByteUtil;

/**
 * Set Chunk Size
 * msid: 0x01
 *
 * @author Drelang
 * @date 2021/3/9 00:06
 */

public class SetChunkSizeMessage extends ProtocolControlMessage{

    private int chunkSize;

    public SetChunkSizeMessage(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    public byte outBoundMessageTypeId() {
        return 0x01;
    }

    @Override
    public byte[] outMessageToBytes() {
        return ByteUtil.convertInt2BytesBE(chunkSize, 4);
    }

    @Override
    public String toReadableString() {
        return null;
    }

    @Override
    public int outMessageLength() {
        return 4;
    }
}

