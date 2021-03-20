package cn.drelang.live.server.rtmp.handler;

import cn.drelang.live.server.rtmp.entity.RtmpMessage;
import cn.drelang.live.server.rtmp.entity.RtmpHeader;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import cn.drelang.live.server.rtmp.message.command.DataMessage;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.drelang.live.server.rtmp.entity.Constants.*;

/**
 * 解析 RTMP Chunk, 主要是解析 Header，body数据原封不动传到下一个 handler。
 *
 * 此处用 ReplayingDecoder 更好，因为 ByteBuf 中的数据流并不是理想的一个一个 RTMP 包，
 * 此时用 ReplayingDecoder 比 ByteToMessageDecoder 更方便编写代码。
 *
 * @author Drelang
 * @date 2021/3/5 21:47
 */
@Slf4j
public class ChunkDecoder extends ReplayingDecoder<ChunkDecoder.State> {

    byte LOW_SIX = (byte)0x3F;
    byte BYTE = (byte)0xFF;

    final int CHUNK_SIZE = 4096;
    /**
     * 管理 stream id
     */
    public final Cache<Integer, RtmpHeader> STREAM_MANAGER =  CacheBuilder.newBuilder()
            .initialCapacity(30)
            .maximumSize(Integer.MAX_VALUE)
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .build();

    /**
     * 管理 ReplayingDecoder 的状态，避免重复读取一个消息
     * 没有使用 State 管理之前，的确是重复读取了推流的 @metaData 消息
     */
    enum State {
        READ_HEADER,   // 准备解析 header
        READ_BODY; // 准备解析 body
    }

    private RtmpHeader currentHeader;

    /**
     * key: csid    value: RtmpHeader
     */
    private final Map<Integer, RtmpHeader> headerManager = new HashMap<>(8);

    /**
     * 当前消息的 payload，不能多也不能少
     */
    private ByteBuf currentPayload;

    public ChunkDecoder() {
        super(State.READ_HEADER);
    }

    /**
     * 只要 in 有数据，就会不断解析
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (state() == State.READ_HEADER) {
            currentHeader = readHeader(in);
            checkpoint(State.READ_BODY);
        } else if (state() == State.READ_BODY) {
            RtmpHeader header = headerManager.get(currentHeader.getChunkStreamId());
            int payloadLength = Math.min(CHUNK_SIZE, header.getMessageLength());
            ByteBuf buf = in.readBytes(payloadLength);
            // must checkpoint() before later action
            checkpoint(State.READ_HEADER);
            switch (header.getMessageTypeId()) {
                case COMMAND_MESSAGE_AMF0: {
                    out.add(new RtmpMessage(header, CommandMessage.createInstance(buf)));
                    break;
                }
                case METADATA_AMF0: {
                    out.add(new RtmpMessage(header, DataMessage.createInstance(buf)));
                    break;
                }
                case SHARED_OBJECT_AMF0: {

                }
                case AGGREGATE_MESSAGE: {

                }
                case AUDIO_MESSAGE: {
                    System.out.println("find audio data");
                    break;
                }
                case VIDEO_MESSAGE: {
                    System.out.println("find video data");
                    break;
                }
                default: ctx.close();
            }
        } else {
            throw new RuntimeException("ChunkDecoder state error " + state());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ChunkDecoder error ", cause);
    }

    private RtmpHeader readHeader(ByteBuf in) {
        byte first = in.readByte();

        // 解析 csid
        int csid = (byte) (first & LOW_SIX);
        if (csid == 0) {    // 需要额外一个字节
            csid = in.readByte() + 64;
        } else if (csid == 1) { // 需要额外两个字节
            csid = in.readShort() + 64;
        }

        // 从缓存中读取 chunkHeader
        RtmpHeader header = headerManager.get(csid);
        if (header == null) {
            header = new RtmpHeader();
            header.setChunkStreamId(csid);
        }

        // 解析 fmt
        byte fmt = (byte) ((first >> 6) & BYTE);
        header.setFmt(fmt);

        if (fmt == 3) {
            return header;
        }

        int timestampDelta = in.readUnsignedMedium();

        if (fmt != 2) {
            // fmt = 0, 1 时，都需要读 message length 和 message type id
            header.setMessageLength(in.readUnsignedMedium());
            header.setMessageTypeId(in.readByte());
            if (fmt == 0) {
                // fmt = 0 时，还要读 message stream id
                header.setMessageStreamId(in.readIntLE());
            }

            headerManager.put(csid, header);
        }

        // 处理可能的 extended timestamp
        if (timestampDelta == 0xFFFFFF){
            timestampDelta = (int)in.readUnsignedInt();
        }

        if (fmt == 0) { // fmt = 0 时，是决定时间戳
            header.setTimeStamp(timestampDelta);
        } else {
            header.setTimeStamp(timestampDelta + header.getTimeStamp());
        }

        return header;
    }

}

