package cn.drelang.live.server.rtmp.amf;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * AMF0 ECMA Array Type, not include type mark
 *
 * @author Drelang
 * @date 2021/3/12 21:59
 */

public class ECMAArray {

    /**
     * 数组长度
     */
    private int length;

    private Map<String, Object> info;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Map<String, Object> getInfo() {
        return info;
    }

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }

}

