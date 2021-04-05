package cn.drelang.live.server.format;

import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.amf.ECMAArray;
import cn.drelang.live.server.rtmp.entity.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * The FLV video file format codec
 *
 * @author Drelang
 * @date 2021/4/5 13:46
 */

public class FLV {

    private static final Logger LOG = LoggerFactory.getLogger(FLV.class);

    FLVHeader header;

    FLVFileBody flvFileBody;

    public FLV(){}

    public FLV(FLVHeader header, FLVFileBody body) {
        this.header = header;
        this.flvFileBody = body;
    }

    static class FLVHeader {

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

    static class FLVFileBody {

        List<Node> content;

        static class Node {
            FLVTag tag;
            long previousTagSize;

            @Override
            public String toString() {
                return "Node{" +
                        "previousTagSize=" + previousTagSize +
                        ", tag=" + tag +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "FLVFileBody{" +
                    "content=" + content +
                    '}';
        }
    }

    static class FLVTag {
        private TAG_TYPE type;

        private int dataSize;

        private int timeStamp;

        private short timeStampExtended;

        private int streamId;

        private Data data;

        enum TAG_TYPE {
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

    static class Data {}

    static class Audio extends Data {

        SOUND_FORMAT soundFormat;

        SAMPLE_RATE sampleRate;

        SOUND_SIZE soundSize;

        SOUND_TYPE soundType;

        AudioData audioData;

        enum SOUND_FORMAT {
            Linear_PCM_PE(0, "Linear PCM, platform endian"),
            ADPCM(1, "ADPCM"),
            MP3(2, "MP3"),
            Linear_PCM_LE(3, "Linear PCM, little endian"),
            Nelly_MOSER_16(4, "Nellymoser 16-kHz mono"),
            Nelly_MOSER_8(5, "Nellymoser 8-kHz mono"),
            Nelly_MOSER(6, "Nellymoser"),
            GA_PCM(7, "G.711 A-law logarithmic PCM"),
            GM_PCM(8, "G.711 mu-law logarithmic PCM"),
            RESERVED(9, "reserved"),
            AAC(10, "AAC"),
            SPEEX(11, "Speex"),
            MP3_8(14, "MP3 8-Khz"),
            DSS(15, "Devices-specific sound")
            ;

            private final byte code;

            private final String desc;

            SOUND_FORMAT(int code, String desc) {
                this.code = (byte)code;
                this.desc = desc;
            }

            public static SOUND_FORMAT getByCode(byte code) {
                for (SOUND_FORMAT sound : SOUND_FORMAT.values()) {
                    if (code == sound.code) {
                        return sound;
                    }
                }
                return null;
            }

            @Override
            public String toString() {
                return "SOUND_FORMAT{" +
                        "code=" + code +
                        ", desc='" + desc + '\'' +
                        '}';
            }
        }

        enum SAMPLE_RATE {
            LOW(0, "5.5-kHz"),
            SHORT(1, "11-kHz"),
            MEDIUM(2, "22-kHz"),
            HIGH(3, "44-kHz")
            ;
            private final byte code;
            private final String desc;

            SAMPLE_RATE(int code, String desc) {
                this.code = (byte)code;
                this.desc = desc;
            }

            public static SAMPLE_RATE getByCode(byte code) {
                for (SAMPLE_RATE sample : SAMPLE_RATE.values()) {
                    if (sample.code == code) {
                        return sample;
                    }
                }
                return null;
            }

            public byte getCode() {
                return code;
            }

            public String getDesc() {
                return desc;
            }

            @Override
            public String toString() {
                return "SAMPLE_RATE{" +
                        "code=" + code +
                        ", desc='" + desc + '\'' +
                        '}';
            }
        }

        enum SOUND_SIZE {
            EIGHT(0, "snd8Bit"),
            SIXTEEN(1, "snd16Bit"),
            ;
            private final byte code;
            private final String desc;

            SOUND_SIZE(int code, String desc) {
                this.code = (byte)code;
                this.desc = desc;
            }

            public static SOUND_SIZE getByCode(byte code) {
                for (SOUND_SIZE sample : SOUND_SIZE.values()) {
                    if (sample.code == code) {
                        return sample;
                    }
                }
                return null;
            }

            public byte getCode() {
                return code;
            }

            public String getDesc() {
                return desc;
            }

            @Override
            public String toString() {
                return "SOUND_SIZE{" +
                        "code=" + code +
                        ", desc='" + desc + '\'' +
                        '}';
            }
        }

        enum SOUND_TYPE {
            MONO(0, "sndMono"),
            STEREO(1, "sndStereo"),
            ;
            private final byte code;
            private final String desc;

            SOUND_TYPE(int code, String desc) {
                this.code = (byte) code;
                this.desc = desc;
            }

            public static SOUND_TYPE getByCode(byte code) {
                for (SOUND_TYPE sample : SOUND_TYPE.values()) {
                    if (sample.code == code) {
                        return sample;
                    }
                }
                return null;
            }

            public byte getCode() {
                return code;
            }

            public String getDesc() {
                return desc;
            }

            @Override
            public String toString() {
                return "SOUND_SIZE{" +
                        "code=" + code +
                        ", desc='" + desc + '\'' +
                        '}';
            }
        }

        static class AudioData {
            byte[] data;
        }

        static class AACAudioData extends AudioData {

            AACPacketType packetType;

            enum AACPacketType {
                HEAD(0, "AAC sequence header"),
                RAW(1, "AAC RAW"),
                ;

                private final byte code;

                private final String desc;

                AACPacketType(int code, String desc) {
                    this.code = (byte)code;
                    this.desc = desc;
                }

                public AACPacketType getByCode(byte code) {
                    for (AACPacketType packet : AACPacketType.values()) {
                        if (code == packet.code) {
                            return packet;
                        }
                    }
                    return null;
                }

                @Override
                public String toString() {
                    return "AACPacketType{" +
                            "code=" + code +
                            ", desc='" + desc + '\'' +
                            '}';
                }
            }
        }

    }

    static class Video extends Data {

        FRAME_TYPE frameType;

        CODEC codec;

        VideoData videoData;

        enum FRAME_TYPE {
            KEY_FRAME(1, "keyframe(for AVC, a seekable frame)"),
            INTER_FRAME(2, "inter frame(for AVC, anon-seekable frame)"),
            DISPOSABLE_INTER_FRAME(3, "disposable inter frame(H.263only)"),
            GENERATED_KEY_FRAME(4, "generated keyframe(reserved for server use only)"),
            VIDEO_INFO_FRAME(5, "video info/command frame"),
            ;

            private final byte code;

            private final String desc;

            FRAME_TYPE(int code, String desc) {
                this.code = (byte)code;
                this.desc = desc;
            }

            public static FRAME_TYPE getByCode(byte code) {
                for (FRAME_TYPE frame : FRAME_TYPE.values()) {
                    if (code == frame.code) {
                        return frame;
                    }
                }
                return null;
            }

            @Override
            public String toString() {
                return "FRAME_TYPE{" +
                        "code=" + code +
                        ", desc='" + desc + '\'' +
                        '}';
            }
        }

        enum CODEC {
            JPEG(1, "JPEG(currently unused)"),
            H263(2, "iSorenson H.263"),
            SCREEN_VIDEO(3, "Screen video"),
            VP6(4, "ON2 VP6"),
            VP6_AC(5, "On2 VP6 with alpha channel"),
            SCREEN_VIDEO_2(6, "Screen video version 2"),
            AVC(7, "AVC")
            ;

            private final byte code;

            private final String desc;

            CODEC(int code, String desc) {
                this.code = (byte)code;
                this.desc = desc;
            }

            public static CODEC getByCode(byte code) {
                for (CODEC codec : CODEC.values()) {
                    if (code == codec.code) {
                        return codec;
                    }
                }
                return null;
            }

            @Override
            public String toString() {
                return "CODEC{" +
                        "code=" + code +
                        ", desc='" + desc + '\'' +
                        '}';
            }
        }


        static class VideoData {
            byte[] data;
        }

        static class AVCVideoPacket extends VideoData {
            AVCPacketType packetType;

            int compositionTime;

            enum AVCPacketType {
                HEAD(0, "AVC sequence header"),
                NALU(1, "AVC NALU"),
                END(2, "AVC end of sequence(lower level NALU sequence ender is not required or supported"),
                ;

                private final byte code;

                private final String desc;

                AVCPacketType(int code, String desc) {
                    this.code = (byte)code;
                    this.desc = desc;
                }

                public static AVCPacketType getByCode(byte code) {
                    for (AVCPacketType packet : AVCPacketType.values()) {
                        if (code == packet.code) {
                            return packet;
                        }
                    }
                    return null;
                }

                @Override
                public String toString() {
                    return "AVCPacketType{" +
                            "code=" + code +
                            ", desc='" + desc + '\'' +
                            '}';
                }
            }
        }
    }

    static class Script extends Data {

        List<ScriptData> objects;

        static class ScriptData {
            String objectName;
            Object objectData;
        }
    }

    public static FLV decode(ByteBuf in) {
        // decode head
        FLVHeader header = new FLVHeader();
        header.signature = String.valueOf((char) in.readByte()) +
                (char) in.readByte() +
                (char) in.readByte();

        header.version = in.readByte();

        byte type = in.readByte();
        header.presentAudio = (type & 0x04) != 0;
        header.presentVideo = (type & 0x01) != 0;

        header.dataOffset = in.readUnsignedInt();

        // decode body
        FLVFileBody body = new FLVFileBody();
        List<FLVFileBody.Node> content = new ArrayList<>();

        while (in.isReadable()) {
            FLVFileBody.Node node = new FLVFileBody.Node();
            node.previousTagSize = in.readUnsignedInt();

            if (in.isReadable()) {  // 最后一个 tag 只有 previousTagSize 而不包含具体 tag 内容
                FLVTag tag = new FLVTag();
                byte tagType = in.readByte();

                tag.type = FLVTag.TAG_TYPE.getByCode(tagType);
                tag.dataSize = in.readUnsignedMedium();
                tag.timeStamp = in.readUnsignedMedium();
                tag.timeStampExtended = in.readUnsignedByte();
                tag.streamId = in.readUnsignedMedium();

                if (tag.type == FLVTag.TAG_TYPE.AUDIO) {
                    tag.data = decodeAudio(in, tag.dataSize);
                } else if (tag.type == FLVTag.TAG_TYPE.VIDEO) {
                    tag.data = decodeVideo(in, tag.dataSize);
                } else if (tag.type == FLVTag.TAG_TYPE.SCRIPT) {
                    tag.data = decodeScript(in, tag.dataSize);
                } else {
                    LOG.error("not supported tag " + tagType);
                }

                node.tag = tag;
            }

            content.add(node);
        }

        body.content = content;

        return new FLV(header, body);
    }

    /**
     *
     * decode one audio data
     *
     * @param in buffer in
     * @param dataLen size of sound data
     * @return {@link Audio}
     */
    private static Audio decodeAudio(ByteBuf in, int dataLen) {
        Audio audio = new Audio();
        byte type = in.readByte();
        audio.soundFormat = Audio.SOUND_FORMAT.getByCode((byte) ((type & 0xF0) >> 4));
        audio.sampleRate = Audio.SAMPLE_RATE.getByCode((byte) ((type & 0x0C) >> 2));
        audio.soundSize = Audio.SOUND_SIZE.getByCode((byte) ((type & 0x02) >> 1));
        audio.soundType = Audio.SOUND_TYPE.getByCode((byte)((type & 0x01)));

        Audio.AudioData audioData;
        if (audio.soundFormat == Audio.SOUND_FORMAT.AAC) {
            Audio.AACAudioData t = new Audio.AACAudioData();
            t.packetType = Audio.AACAudioData.AACPacketType.HEAD.getByCode(in.readByte());
            t.data = readAll(in, dataLen - 2);
            audioData = t;
        } else {
            audioData = new Audio.AudioData();
            audioData.data = readAll(in, dataLen - 1);
        }

        audio.audioData = audioData;
        return audio;
    }

    /**
     * decode one video data
     *
     * @param in buffer in
     * @param dataLen size of sound data
     * @return {@link Video}
     */
    private static Video decodeVideo(ByteBuf in, int dataLen) {
        Video video = new Video();
        byte first = in.readByte();
        video.frameType = Video.FRAME_TYPE.getByCode((byte) ((first & 0xF0) >> 4));
        video.codec = Video.CODEC.getByCode((byte) (first & 0x0F));

        Video.VideoData videoData;
        if (video.codec == Video.CODEC.AVC) {
            Video.AVCVideoPacket packet = new Video.AVCVideoPacket();
            packet.packetType = Video.AVCVideoPacket.AVCPacketType.getByCode(in.readByte());
            packet.compositionTime = in.readUnsignedMedium();
            packet.data = readAll(in, dataLen - 5);
            videoData = packet;
        } else {
            videoData = new Video.VideoData();
            videoData.data = readAll(in, dataLen - 1);
        }
        video.videoData = videoData;
        return video;
    }

    private static Script decodeScript(ByteBuf in, int dataLen) {
        Script script = new Script();
        List<Script.ScriptData> data = new ArrayList<>();

        while (in.getUnsignedMedium(in.readerIndex()) != Constants.AMF0_OBJECT_END) {
            Script.ScriptData scriptData = new Script.ScriptData();
            scriptData.objectName = (String) AMF0.decodeAMF0Type(in);
            scriptData.objectData = AMF0.decodeAMF0Type(in);
            data.add(scriptData);

            if (scriptData.objectData instanceof ECMAArray) {   // 出现 ECMA Array 时，只有一个 end marker
                break;
            }
        }

        script.objects = data;
        return script;
    }

    public static byte[] readAll(ByteBuf in, int len) {
        byte[] r = new byte[len];
        in.readBytes(r);
        return r;
    }

    public static void encode(OutputStream out, FLVFileBody.Node node, boolean includeHeader) throws IOException {
        if (includeHeader) {
            encodeFLVHeader(out);
        }
        encode(out, node);
    }

    /**
     * encode flv data node to byte stream or store into file
     * @param out When FileOutputStream, must append!
     * @param node one data node
     * @throws IOException IOException
     */
    public static void encode(OutputStream out, FLVFileBody.Node node) throws IOException {
        FLVTag tag = node.tag;
        ByteBuf buf;
        if (tag == null) {  // 最后一个没有 tag，只有 previousTagSize
            buf = Unpooled.buffer(4);
            buf.writeInt((int)node.previousTagSize);
            out.write(readAll(buf, buf.readableBytes()));
            return ;
        }

        buf = Unpooled.buffer(tag.dataSize+15);
        buf.writeInt((int)node.previousTagSize);
        buf.writeByte(tag.type.getType());
        buf.writeMedium(tag.dataSize);
        buf.writeMedium(tag.timeStamp);
        buf.writeByte(tag.timeStampExtended);
        buf.writeMedium(tag.streamId);

        Data data = tag.data;
        if (data instanceof Video) {
            byte first = ((Video) data).frameType.code;
            first <<= 4;
            first ^= ((Video) data).codec.code;
            buf.writeByte(first);

            Video.VideoData videoData = ((Video) data).videoData;
            if (videoData instanceof Video.AVCVideoPacket) {
                buf.writeByte(((Video.AVCVideoPacket) videoData).packetType.code);
                buf.writeMedium(((Video.AVCVideoPacket) videoData).compositionTime);
                buf.writeBytes(videoData.data);
            } else {
                buf.writeBytes(videoData.data);
            }
        } else if (data instanceof Audio) {
            byte first = ((Audio) data).soundFormat.code;
            first <<= 4;
            first ^= ((Audio) data).sampleRate.code << 2;
            first ^= ((Audio) data).soundSize.code << 1;
            first ^= ((Audio) data).soundType.code;
            buf.writeByte(first);

            Audio.AudioData audioData = ((Audio) data).audioData;
            if (audioData instanceof Audio.AACAudioData) {
                buf.writeByte(((Audio.AACAudioData) audioData).packetType.code);
                buf.writeBytes(audioData.data);
            } else {
                buf.writeBytes(audioData.data);
            }
        } else if (data instanceof Script) {
            List<Script.ScriptData> objects = ((Script) data).objects;
            for (Script.ScriptData scriptData : objects) {
                buf.writeBytes(AMF0.encodeAMF0Type(scriptData.objectName));
                buf.writeBytes(AMF0.encodeAMF0Type(scriptData.objectData));
            }
        } else {
            LOG.error("unsupported type {}", tag.type);
        }

        out.write(readAll(buf, buf.readableBytes()));
    }

    public static void encodeFLVHeader(OutputStream out) throws IOException {
        out.write("FLV".getBytes(StandardCharsets.UTF_8));  // signature
        out.write(0x01);    // version
        out.write(0x05);    // audio flag, video flag
        out.write(new byte[]{0x00, 0x00, 0x00, 0x09});    // size of the header
    }

    public static void main(String[] args) throws IOException {
        File file = new File("sample.flv");
        FileInputStream inputStream = new FileInputStream(file);
        FileChannel fileChannel = inputStream.getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        ByteBuf in = Unpooled.wrappedBuffer(mappedByteBuffer);

        FLV flv = decode(in);

        FileOutputStream out = new FileOutputStream("out.flv", true);
        encodeFLVHeader(out);
        for (FLVFileBody.Node node : flv.flvFileBody.content) {
            encode(out, node);
        }

    }

}
