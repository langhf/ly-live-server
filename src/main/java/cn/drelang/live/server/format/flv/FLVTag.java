package cn.drelang.live.server.format.flv;

/**
 *
 * @author Drelang
 * @date 2021/4/5 20:31
 */

public class FLVTag {
    TAG_TYPE type;

    int dataSize;

    int timeStamp;

    short timeStampExtended;

    int streamId;

    FLVData data;

    public void setType(TAG_TYPE type) {
        this.type = type;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setTimeStampExtended(short timeStampExtended) {
        this.timeStampExtended = timeStampExtended;
    }

    public void setStreamId(int streamId) {
        this.streamId = streamId;
    }

    public void setData(FLVData data) {
        this.data = data;
    }

    public enum TAG_TYPE {
        AUDIO((byte) 0x08),
        VIDEO((byte) 0x09),
        SCRIPT((byte) 0x12)
        ;
        private final byte type;

        TAG_TYPE(byte type) {
            this.type = type;
        }

        public byte getType() {
            return type;
        }

        public static TAG_TYPE getByCode(byte code) {
            for (TAG_TYPE tag : TAG_TYPE.values()) {
                if (tag.type == code) {
                    return tag;
                }
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return "FLVTag{" +
                "type=" + type +
                ", dataSize=" + dataSize +
                ", timeStamp=" + timeStamp +
                ", timeStampExtended=" + timeStampExtended +
                ", streamId=" + streamId +
                ", data=" + data +
                '}';
    }
}
