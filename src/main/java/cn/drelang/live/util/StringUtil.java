package cn.drelang.live.util;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Drelang
 * @date 2021/3/7 15:52
 */

public class StringUtil {

    public static final byte READABLE_MIN = 0x21;
    public static final byte READABLE_MAX = 0x7E;

    public static String random(int len) {
        byte[] out = new byte[len];
        new Random().nextBytes(out);
        return Arrays.toString(out);
    }
}

