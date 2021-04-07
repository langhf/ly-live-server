package cn.drelang.live.server.format.flv;

import java.util.List;

/**
 *
 * @author Drelang
 * @date 2021/4/5 20:33
 */

public class FLVData {

    public static class Audio extends FLVData {

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

            final byte code;

            final String desc;

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
            final byte code;
            final String desc;

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
            final byte code;
            final String desc;

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
            final byte code;
            final String desc;

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

                final byte code;

                final String desc;

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

    public static class Video extends FLVData {

        FRAME_TYPE frameType;

        CODEC codec;

        VideoData videoData;

        public enum FRAME_TYPE {
            KEY_FRAME(1, "keyframe(for AVC, a seekable frame)"),
            INTER_FRAME(2, "inter frame(for AVC, anon-seekable frame)"),
            DISPOSABLE_INTER_FRAME(3, "disposable inter frame(H.263only)"),
            GENERATED_KEY_FRAME(4, "generated keyframe(reserved for server use only)"),
            VIDEO_INFO_FRAME(5, "video info/command frame"),
            ;

            final byte code;

            final String desc;

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

            final byte code;

            final String desc;

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


        public static class VideoData {
            byte[] data;

            public void setData(byte[] data) {
                this.data = data;
            }

            public byte[] getData() {
                return data;
            }
        }

        public static class AVCVideoPacket extends VideoData {
            AVCPacketType packetType;

            int compositionTime;

            enum AVCPacketType {
                HEAD(0, "AVC sequence header"),
                NALU(1, "AVC NALU"),
                END(2, "AVC end of sequence(lower level NALU sequence ender is not required or supported"),
                ;

                final byte code;

                final String desc;

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

    public static class Script extends FLVData {

        List<ScriptData> objects;

        public void setObjects(List<ScriptData> objects) {
            this.objects = objects;
        }

        public static class ScriptData {
            String objectName;
            Object objectData;

            public void setObjectName(String objectName) {
                this.objectName = objectName;
            }

            public void setObjectData(Object objectData) {
                this.objectData = objectData;
            }
        }
    }
}

