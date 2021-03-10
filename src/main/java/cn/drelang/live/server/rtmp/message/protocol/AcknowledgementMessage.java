package cn.drelang.live.server.rtmp.message.protocol;

import cn.drelang.live.util.ByteUtil;

/**
 * AcknowledgementMessage, 迄今为止收到的字节数
 *
 * @author Drelang
 * @date 2021/3/10 23:03
 */

public class AcknowledgementMessage extends ProtocolControlMessage {

    /**
     * This message specifies the sequence number, which is the number of the bytes received so far.
     */
    private final int sequenceNumber;

    public AcknowledgementMessage(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public byte outBoundMessageTypeId() {
        return 0x03;
    }

    @Override
    public byte[] outMessageToBytes() {
        return ByteUtil.convertInt2BytesBE(sequenceNumber, 4);
    }

    @Override
    public String toReadableString() {
        return "AcknowledgementMessage{sequenceNumber=" + sequenceNumber + "}";
    }

    @Override
    public int outMessageLength() {
        return 4;
    }
}

