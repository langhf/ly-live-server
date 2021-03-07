package cn.drelang.live.util;

import java.nio.ByteBuffer;

/**
 * TODO:
 *
 * @author Drelang
 * @date 2021/3/6 16:43
 */

public class ByteUtil {

    private static final int LOW_FIRST_BYTE =  0x000000FF;
    private static final int LOW_SECOND_BYTE = 0x0000FF00;
    private static final int LOW_THIRD_BYTE =  0x00FF0000;

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

    // int to byte array, big endian
    public static byte[] convertInt2BytesBE(int in, int len) {
        byte[] ret = convertInt2BytesLE(in, len);

        for (int i = 0; i < ret.length / 2; i++) {  // to big endian
            byte t = ret[i];
            ret[i] = ret[ret.length - i - 1];
            ret[ret.length -i -1] = t;
        }
        return ret;
    }

    // int to byte array, little endian
    public static byte[] convertInt2BytesLE(int in, int len) {
        assert len > 0 && len <= 4;

        byte[] ret = new byte[len];
        int match = 0xFF;
        for (int i = 0; i < len; i++) {
            ret[i] = (byte) (in & match);
            in >>= 8;
        }
        return ret;
    }

    public static byte[] asByteArray(double in) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
        byteBuffer.putDouble(in);
        return byteBuffer.array();
    }

    public static byte[] mergeByteArray(byte[]... in) {
        int count = 0;
        for(byte[] i : in) {
            count += i.length;
        }
        byte[] out = new byte[count];
        int idx = 0;
        for(byte[] i : in) {
            System.arraycopy(i, 0, out, idx, i.length);
            idx += i.length;
        }
        return out;
    }

    public static void main(String[] args) {
        byte[] b1 = new byte[]{0x00, 0x01, 0x02};
        byte[] b2 = new byte[]{0x00, 0x01, 0x02};
        byte[] b3 = new byte[]{0x00, 0x01, 0x02};
        mergeByteArray(b1, b2, b3);
    }

}

