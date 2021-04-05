package cn.drelang.live.server.format.flv;

import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.amf.ECMAArray;
import cn.drelang.live.server.rtmp.entity.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.font.Script;

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
     * @return {@link FLVData}
     */
    public static FLVData.Audio decodeAudio(ByteBuf in, int dataLen) {
        FLVData.Audio audio = new FLVData.Audio();
        byte type = in.readByte();
        audio.soundFormat = FLVData.Audio.SOUND_FORMAT.getByCode((byte) ((type & 0xF0) >> 4));
        audio.sampleRate = FLVData.Audio.SAMPLE_RATE.getByCode((byte) ((type & 0x0C) >> 2));
        audio.soundSize = FLVData.Audio.SOUND_SIZE.getByCode((byte) ((type & 0x02) >> 1));
        audio.soundType = FLVData.Audio.SOUND_TYPE.getByCode((byte)((type & 0x01)));

        FLVData.Audio.AudioData audioData;
        if (audio.soundFormat == FLVData.Audio.SOUND_FORMAT.AAC) {
            FLVData.Audio.AACAudioData t = new FLVData.Audio.AACAudioData();
            t.packetType = FLVData.Audio.AACAudioData.AACPacketType.HEAD.getByCode(in.readByte());
            t.data = readAll(in, dataLen - 2);
            audioData = t;
        } else {
            audioData = new FLVData.Audio.AudioData();
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
     * @return {@link FLVData}
     */
    public static FLVData.Video decodeVideo(ByteBuf in, int dataLen) {
        FLVData.Video video = new FLVData.Video();
        byte first = in.readByte();
        video.frameType = FLVData.Video.FRAME_TYPE.getByCode((byte) ((first & 0xF0) >> 4));
        video.codec = FLVData.Video.CODEC.getByCode((byte) (first & 0x0F));

        FLVData.Video.VideoData videoData;
        if (video.codec == FLVData.Video.CODEC.AVC) {
            FLVData.Video.AVCVideoPacket packet = new FLVData.Video.AVCVideoPacket();
            packet.packetType = FLVData.Video.AVCVideoPacket.AVCPacketType.getByCode(in.readByte());
            packet.compositionTime = in.readUnsignedMedium();
            packet.data = readAll(in, dataLen - 5);
            videoData = packet;
        } else {
            videoData = new FLVData.Video.VideoData();
            videoData.data = readAll(in, dataLen - 1);
        }
        video.videoData = videoData;
        return video;
    }

    public static FLVData.Script decodeScript(ByteBuf in, int dataLen) {
        FLVData.Script script = new FLVData.Script();
        List<FLVData.Script.ScriptData> data = new ArrayList<>();

        while (in.getUnsignedMedium(in.readerIndex()) != Constants.AMF0_OBJECT_END) {
            FLVData.Script.ScriptData scriptData = new FLVData.Script.ScriptData();
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

        FLVData data = tag.data;
        if (data instanceof FLVData.Video) {
            buf.writeBytes(encodeVideo((FLVData.Video) data));
        } else if (data instanceof FLVData.Audio) {
            buf.writeBytes(encodeAudio((FLVData.Audio) data));
        } else if (data instanceof FLVData.Script) {
            buf.writeBytes(encodeScript((FLVData.Script) data));
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

    public static byte[] encodeAudio(FLVData.Audio audio) {
        ByteBuf buf = Unpooled.buffer();
        byte first = audio.soundFormat.code;
        first <<= 4;
        first ^= audio.sampleRate.code << 2;
        first ^= audio.soundSize.code << 1;
        first ^= audio.soundType.code;
        buf.writeByte(first);

        FLVData.Audio.AudioData audioData = audio.audioData;
        if (audioData instanceof FLVData.Audio.AACAudioData) {
            buf.writeByte(((FLVData.Audio.AACAudioData) audioData).packetType.code);
            buf.writeBytes(audioData.data);
        } else {
            buf.writeBytes(audioData.data);
        }

        return ByteBufUtil.getBytes(buf);
    }

    public static byte[] encodeVideo(FLVData.Video video) {
        ByteBuf buf = Unpooled.buffer();
        byte first = video.frameType.code;
        first <<= 4;
        first ^= video.codec.code;
        buf.writeByte(first);

        FLVData.Video.VideoData videoData = video.videoData;
        if (videoData instanceof FLVData.Video.AVCVideoPacket) {
            buf.writeByte(((FLVData.Video.AVCVideoPacket) videoData).packetType.code);
            buf.writeMedium(((FLVData.Video.AVCVideoPacket) videoData).compositionTime);
            buf.writeBytes(videoData.data);
        } else {
            buf.writeBytes(videoData.data);
        }

        return ByteBufUtil.getBytes(buf);
    }

    public static byte[] encodeScript(FLVData.Script script) {
        ByteBuf buf = Unpooled.buffer();
        List<FLVData.Script.ScriptData> objects = script.objects;
        for (FLVData.Script.ScriptData scriptData : objects) {
            buf.writeBytes(AMF0.encodeAMF0Type(scriptData.objectName));
            buf.writeBytes(AMF0.encodeAMF0Type(scriptData.objectData));
        }

        return ByteBufUtil.getBytes(buf);
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
