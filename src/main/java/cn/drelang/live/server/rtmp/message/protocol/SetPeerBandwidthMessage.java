package cn.drelang.live.server.rtmp.message.protocol;

import cn.drelang.live.util.ByteUtil;

/**
 * 设置对等带宽大小
 * typeId：6
 *
 * payload：
 *  4 字节 Acknowledgement Window Size
 *  1 字节 Limit Type
 *      0 - Hard
 *      1 - Soft
 *      2 - Dynamic
 *
 * @author Drelang
 * @date 2021/3/7 20:09
 */

public class SetPeerBandwidthMessage extends ProtocolControlMessage{

    private int acknowledgementWindowSize;

    private byte limitType;

    public SetPeerBandwidthMessage() {}

    public SetPeerBandwidthMessage(int acknowledgementWindowSize, byte limitType) {
        this.acknowledgementWindowSize = acknowledgementWindowSize;
        this.limitType = limitType;
    }

    @Override
    public byte outMessageTypeId() {
        return 0x06;
    }

    @Override
    public int outMessageLength() {
        return Integer.BYTES + Byte.BYTES;
    }

    @Override
    public byte[] messageToBytes() {
        byte[] ackWindowSize = ByteUtil.convertInt2BytesBE(acknowledgementWindowSize, 4);
        return ByteUtil.mergeByteArray(ackWindowSize, new byte[]{limitType});
    }
}
