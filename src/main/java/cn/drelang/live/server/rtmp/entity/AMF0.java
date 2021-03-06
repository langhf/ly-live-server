package cn.drelang.live.server.rtmp.entity;

import cn.drelang.live.util.ByteUtil;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static cn.drelang.live.server.rtmp.entity.Constants.*;

/**
 *
 * @author Drelang
 * @date 2021/3/6 16:26
 */

public class AMF0 {

    static byte[] DOUBLE_BYTE = new byte[2];
    static byte[] TRIPLE_BYTE = new byte[3];

    public static AMFCommandMessage decodeCommandMessage(byte[] in) {
        ByteBuf buf = Unpooled.copiedBuffer(in);

        AMFCommandMessage message = new AMFCommandMessage();
        message.setCommandName((String)decodeAMF0Type(buf.readByte(), buf));
        message.setTransactionID((Double) decodeAMF0Type(buf.readByte(), buf));
        message.setObjectMap((Map<String, Object>)decodeAMF0Type(buf.readByte(), buf));
        return message;
    }

    private static Object decodeAMF0Type(byte type, ByteBuf buf) {
        switch (type) {
            case AMF0_NUMBER:
                return buf.readDouble();
            case AMF0_BOOLEAN:
                return buf.readByte() != 0x00;
            case AMF0_STRING:
                buf.readBytes(DOUBLE_BYTE);
                int len = ByteUtil.convertBytesToInt(DOUBLE_BYTE);
                byte[] s = new byte[len];
                buf.readBytes(s);
                return new String(s, StandardCharsets.UTF_8);
            case AMF0_OBJECT:
                Map<String, Object> map = Maps.newHashMap();
                while (!findObjectEndMarker(buf)) {
                    // 对于 key，一定是 String 类型，因此省略了 key 的 marker
                    String key = (String)decodeAMF0Type(AMF0_STRING, buf);
                    // value 类型多样
                    Object value = decodeAMF0Type(buf.readByte(), buf);
                    map.put(key, value);
                }
                // 探测到 Object End 后，需要将读指针向后移动3位
                buf.readerIndex(buf.readerIndex()+3);
                return map;
            case AMF0_ECMA_ARRAY:
                int el = buf.readInt();
                return null;
            case AMF0_XML_DOCUMENT:
            case AMF0_LONG_STRING:
                int ll = buf.readInt();
                byte[] ls = new byte[ll];
                buf.readBytes(ls);
                return new String(ls, StandardCharsets.UTF_8);
            case AMF0_MOVIE_CLIP:
            case AMF0_NULL:
            case AMF0_UNDEFINED:
            case AMF0_REFERENCE:
            case AMF0_OBJECT_END:
            case AMF0_DATE:
            case AMF0_UNSUPPORTED:
            case AMF0_RECORDSET:
            case AMF0_TYPED_OBJECT:
            case AMF0_AVMPLUS_OBJECT:
            default:
                return null;
        }
    }

    // 寻找 Object 结束标志  0x000009
    private static boolean findObjectEndMarker(ByteBuf buf) {
        buf.getBytes(buf.readerIndex(), TRIPLE_BYTE);
        return ByteUtil.convertBytesToInt(TRIPLE_BYTE) == AMF0_OBJECT_END;
    }
}

