package cn.drelang.live.server.format.flv;

/**
 *
 * @author Drelang
 * @date 2021/4/5 20:30
 */

public class FLVHeader {

    String signature;

    byte version;

    boolean presentAudio;

    boolean presentVideo;

    long dataOffset;

    @Override
    public String toString() {
        return "FLVHeader{" +
                "signature='" + signature + '\'' +
                ", version=" + version +
                ", presentAudio=" + presentAudio +
                ", presentVideo=" + presentVideo +
                ", dataOffset=" + dataOffset +
                '}';
    }
}

