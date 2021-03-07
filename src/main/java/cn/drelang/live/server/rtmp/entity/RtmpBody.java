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

    byte[] messageToBytes();
}

