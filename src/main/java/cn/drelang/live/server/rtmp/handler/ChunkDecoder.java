package cn.drelang.live.server.rtmp.handler;

import cn.drelang.live.server.rtmp.entity.RtmpMessage;
import cn.drelang.live.server.rtmp.entity.RtmpHeader;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import cn.drelang.live.server.rtmp.message.command.netconnection.ConnectMessage;
import cn.drelang.live.util.ByteUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
public class ChunkDecoder extends ReplayingDecoder<Void> {

    byte LOW_SIX = (byte)0x3F;
    byte BYTE = (byte)0xFF;

    byte[] DOUBLE_BYTE = new byte[2];
    byte[] TRIPLE_BYTE = new byte[3];
    byte[] ONE_WORD = new byte[4];
    /**
     * 管理 stream id
     */
    public final Cache<Integer, RtmpHeader> STREAM_MANAGER;

    {
        STREAM_MANAGER = CacheBuilder.newBuilder()
                .initialCapacity(30)
                .maximumSize(Integer.MAX_VALUE)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        RtmpHeader header = readHeader(in);

        switch (header.getMessageTypeId()) {
            case COMMAND_MESSAGE_AMF0: {
                CommandMessage commandMessage = CommandMessage.decodeCMDNameAndTXID4AMF0(in);
                String commandName = commandMessage.getCommandName();
                if (commandName.equals("connect")) {
                    ConnectMessage connectMessage = commandMessage.outputSub(ConnectMessage.class);
                    connectMessage.decodeArguments4AMF0(in);
                    commandMessage = connectMessage;
                }
                out.add(new RtmpMessage(header, commandMessage));
                break;
            }
            case AUDIO_MESSAGE: {

            }
            case VIDEO_MESSAGE: {

            }
            case METADATA_AMF0: {

            }
            case SHARED_OBJECT_AMF0: {

            }
            case AGGREGATE_MESSAGE: {

            }

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
            in.readBytes(DOUBLE_BYTE);
            csid = ByteUtil.convertBytesToInt(DOUBLE_BYTE) + 64;
        }

        // 从缓存中读取 chunkHeader
        RtmpHeader header = STREAM_MANAGER.getIfPresent(csid);
        if (header == null) {
            header = new RtmpHeader();
            header.setChannelStreamId(csid);
        }

        header.setChannelStreamId(csid);

        // 解析 fmt
        byte fmt = (byte) ((first >> 6) & BYTE);
        header.setFmt(fmt);

        if (fmt == 3) {
            return header;
        }

        in.readBytes(TRIPLE_BYTE);
        int timestampDelta = ByteUtil.convertBytesToInt(TRIPLE_BYTE);

        if (fmt != 2) {
            // fmt = 0, 1 时，都需要读 message length 和 message type id
            in.readBytes(TRIPLE_BYTE);
            header.setMessageLength(ByteUtil.convertBytesToInt(TRIPLE_BYTE));
            header.setMessageTypeId(in.readByte());
            if (fmt == 0) {
                // fmt = 0 时，还要读 message stream id
                in.readBytes(ONE_WORD);
                header.setMessageStreamId(ByteUtil.convertBytesToInt(ONE_WORD));
            }

            STREAM_MANAGER.put(csid, header);
        }

        // 处理可能的 extended timestamp
        if (timestampDelta == 0xFFFFFF){
            in.readBytes(ONE_WORD);
            timestampDelta = ByteUtil.convertBytesToInt(ONE_WORD);
        }

        if (fmt == 0) { // fmt = 0 时，是决定时间戳
            header.setTimeStamp(timestampDelta);
        } else {
            header.setTimeStamp(timestampDelta + header.getTimeStamp());
        }

        return header;
    }

}

