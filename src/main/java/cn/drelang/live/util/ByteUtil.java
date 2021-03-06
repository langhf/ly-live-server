package cn.drelang.live.util;

/**
 * TODO:
 *
 * @author Drelang
 * @date 2021/3/6 16:43
 */

public class ByteUtil {
    // big endian
    public static int convertBytesToInt(byte[] bytes) {
        if (bytes != null && bytes.length > 4) {
            throw new RuntimeException("too big for int");
        }
        return (int) convert(bytes);
    }

    public static long convertBytesToLong(byte[] bytes) {
        if (bytes != null && bytes.length > 8) {
            throw new RuntimeException("too big for int");
        }
        return convert(bytes);
    }

    private static long convert(byte[] bytes) {
        int ret = 0;

        if (bytes == null || bytes.length < 1) {
            return ret;
        }

        for (int i = 0; i < bytes.length; i++) {
            ret = ret ^ bytes[i];
            if (i < bytes.length - 1) { // 到达最后一位不用左移
                ret = ret << 8;
            }
        }

        return ret;
    }
}

