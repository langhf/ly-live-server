package cn.drelang.live.server.rtmp.entity;

import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.util.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 * @author Drelang
 * @date 2021/3/7 15:28
 */
public class AMF0Test {

    @Test
    public void testNumber() {
        // 00, 3F, F0, 00, 00, 00, 00, 00, 00
        double rawDouble = 1.0;
        byte[] bytes = AMF0.encodeAMF0Type(rawDouble);
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        Double decodedDouble = (Double) AMF0.decodeAMF0Type(buf);
        assertEquals(rawDouble, decodedDouble, 0.0000001);

        long rawLong = 1L;
        bytes = AMF0.encodeAMF0Type(rawLong);
        buf = Unpooled.copiedBuffer(bytes);
        Double decodeDouble1 = (Double) AMF0.decodeAMF0Type(buf);
        assertEquals(rawLong, decodeDouble1.longValue());

        int rawInt = 1;
        bytes = AMF0.encodeAMF0Type(rawInt);
        buf = Unpooled.copiedBuffer(bytes);
        Double decodeDouble2 = (Double) AMF0.decodeAMF0Type(buf);
        assertEquals(rawInt, decodeDouble2.longValue());
    }

    @Test
    public void testString() {
        String shortString = "xly";
        byte[] bytes = AMF0.encodeAMF0Type(shortString);
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        String decodedShortString = (String) AMF0.decodeAMF0Type(buf);
        assertEquals(shortString, decodedShortString);

        String longString = StringUtil.random(66000);
        bytes = AMF0.encodeAMF0Type(longString);
        buf = Unpooled.copiedBuffer(bytes);
        String decodedLongString = (String) AMF0.decodeAMF0Type(buf);
        assertEquals(longString, decodedLongString);
    }

    @Test
    public void testMap() {
        // [3, 2, 0, 4, 110, 97, 109, 101, 2, 0, 3, 120, 108, 121, 2, 0, 3, 97, 103, 101, 0, 63, -16, 0, 0, 0, 0, 0, 0, 0, 0, 9]
        Map<String, Object> rawMap = new HashMap<String, Object>() {{
            put("name", "xly");
            put("age", 1.0);
        }};

        byte[] bytes = AMF0.encodeAMF0Type(rawMap);
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        Map<String, Object> decodedMap = (Map<String, Object>) AMF0.decodeAMF0Type(buf);
        assertEquals(rawMap, decodedMap);
    }

}