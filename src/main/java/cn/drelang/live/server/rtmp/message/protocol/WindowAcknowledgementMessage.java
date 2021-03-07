package cn.drelang.live.server.rtmp.message.protocol;

import cn.drelang.live.server.rtmp.entity.Constants;
import cn.drelang.live.util.ByteUtil;

/**
 * window acknowledgement message
 *
 * payload：四个字节长度的整形数
 *
 * @author Drelang
 * @date 2021/3/7 17:53
 */

public class WindowAcknowledgementMessage extends ProtocolControlMessage {
    /**
     * 窗口大小
     */
    private int windowSize;

    public WindowAcknowledgementMessage() {}

    public WindowAcknowledgementMessage(int windowSize) {
        this.windowSize = windowSize;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    @Override
    public byte outMessageTypeId() {
        return Constants.ACKNOWLEDGEMENT_WINDOW_SIZE;
    }

    @Override
    public int outMessageLength() {
        return 4;
    }

    @Override
    public String toString() {
        return super.toString() +
                "windowSize=" + windowSize;
    }

    @Override
    public byte[] messageToBytes() {
        return ByteUtil.convertInt2BytesBE(windowSize, 4);
    }
}

