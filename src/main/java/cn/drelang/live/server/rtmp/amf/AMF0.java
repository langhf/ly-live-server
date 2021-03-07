package cn.drelang.live.server.rtmp.amf;

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

    /**
     * 将 AMF0 字节数组解码为 java 可读类型，只读取 buf 中的第一个 AMF0 类型
     * @param buf ByteBuf 类型，方面读完顺便移动读指针
     * @return Object 类型，调用方需要进行强制转换
     */
    public static Object decodeAMF0Type(ByteBuf buf) {
        byte type = buf.readByte();
        switch (type) {
            case AMF0_NUMBER:
                return buf.readDouble();
            case AMF0_BOOLEAN:
                return buf.readByte() != 0x00;
            case AMF0_STRING:
                return decode2ShortString(buf);
            case AMF0_OBJECT:
                Map<String, Object> map = Maps.newHashMap();
                while (!findObjectEndMarker(buf)) {
                    // 注意，此处略坑！ 对于 key，一定是 String 类型，因此省略了 key 的 marker
                    String key = decode2ShortString(buf);
                    // value 类型多样
                    Object value = decodeAMF0Type(buf);
                    map.put(key, value);
                }
                // 探测到 Object End 后，需要将读指针向后移动3位
                buf.skipBytes(3);
                return map;
            case AMF0_LONG_STRING:
                int ll = buf.readInt();
                byte[] ls = new byte[ll];
                buf.readBytes(ls);
                return new String(ls, StandardCharsets.UTF_8);
            case AMF0_ECMA_ARRAY:
                int el = buf.readInt();
                return null;
            case AMF0_XML_DOCUMENT:
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

    /**
     * 不含 shortString 的 marker，直接从 length 和 content 开始的 buf
     */
    private static String decode2ShortString(ByteBuf buf) {
        byte[] t = new byte[2];
        buf.readBytes(t);
        int len = ByteUtil.convertBytesToInt(t);
        byte[] s = new byte[len];
        buf.readBytes(s);
        return new String(s, StandardCharsets.UTF_8);
    }

    // 寻找 Object 结束标志  0x000009
    private static boolean findObjectEndMarker(ByteBuf buf) {
        byte[] t = new byte[3];
        buf.getBytes(buf.readerIndex(), t);
        return ByteUtil.convertBytesToInt(t) == AMF0_OBJECT_END;
    }

    /**
     * 将 java 可读类型编码成 AMF0 字节数组
     */
    public static byte[] encodeAMF0Type(Object in) {
        ByteBuf out = Unpooled.buffer();
        if (in instanceof Double  || in instanceof Long || in instanceof Integer) {
            out.writeByte(AMF0_NUMBER);
            double dbl = Double.parseDouble(String.valueOf(in));
            out.writeBytes(ByteUtil.asByteArray(dbl));
        } else if (in instanceof Boolean) {
            out.writeByte(AMF0_BOOLEAN);
            boolean bln = (boolean) in;
            if (bln) {
                out.writeByte(0x01);
            } else {
                out.writeByte(0x00);
            }
        } else if (in instanceof String) {
            String as = (String) in;
            if (as.length() > 65535) {
                out.writeByte(AMF0_LONG_STRING);
                out.writeBytes(ByteUtil.convertInt2BytesBE(as.length(), 4));
            } else {
                out.writeByte(AMF0_STRING);
                out.writeBytes(ByteUtil.convertInt2BytesBE(as.length(), 2));
            }
            out.writeBytes(as.getBytes(StandardCharsets.UTF_8));
        } else if (in instanceof Map) {
            out.writeByte(AMF0_OBJECT);
            Map<String, Object> map = (Map) in;
            map.forEach((k, v) -> {
                // 同样此处略坑，需要省略 key 的 String marker
                out.writeBytes(ByteUtil.convertInt2BytesBE(k.length(), 2));
                out.writeBytes(k.getBytes(StandardCharsets.UTF_8));
                out.writeBytes(encodeAMF0Type(v));
            });
            out.writeByte(0x00);
            out.writeByte(0x00);
            out.writeByte(AMF0_OBJECT_END);
        } else {
            throw new RuntimeException("Object type not supported Object=" + in.toString());
        }
        byte[] res = new byte[out.readableBytes()];
        out.readBytes(res);
        return res;
    }
}

