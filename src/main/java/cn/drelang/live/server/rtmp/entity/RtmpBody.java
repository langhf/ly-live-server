package cn.drelang.live.server.rtmp.entity;

/**
 * RTMP Body
 *
 * 定义此类使代码更易读
 *
 * @author Drelang
 * @date 2021/3/5 21:59
 */

public interface RtmpBody {

    /**
     * 出站时的 Message Type Id
     */
    byte outBoundMessageTypeId();

    // inMessageToBytes()

    /**
     * 将出站消息转换为 rtmp 协议格式的字节数组
     */
    byte[] outMessageToBytes();

    /**
     * 转成人类可读字符串
     */
    String toReadableString();
}

