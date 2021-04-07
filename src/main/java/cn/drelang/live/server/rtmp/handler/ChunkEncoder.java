package cn.drelang.live.server.rtmp.handler;

import cn.drelang.live.server.rtmp.entity.RtmpHeader;
import cn.drelang.live.server.rtmp.entity.RtmpMessage;
import cn.drelang.live.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 统一输出: RtmpMessage -> Bytes
 *
 * @author Drelang
 * @date 2021/3/6 14:58
 */

@Slf4j
public class ChunkEncoder extends MessageToByteEncoder<List<RtmpMessage>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, List<RtmpMessage> messages, ByteBuf out) throws Exception {
        ByteBuf r = Unpooled.buffer();
        for (RtmpMessage message : messages) {
            wrapMessage(message, r);
        }
        ctx.writeAndFlush(r);
    }

    private void wrapMessage(RtmpMessage msg, ByteBuf out) {
        int outChunkSize = 128;
        RtmpHeader header = msg.getHeader();

        int msgLen = header.getMessageLength();
        if (msgLen <= outChunkSize) {
            wrapByFmt((byte)0, header, out);
            out.writeBytes(msg.getBody().outMessageToBytes());
        } else {    // 需要分多个 Chunk
            wrapByFmt((byte)0, header, out);
            int toWrite = Math.min(msgLen, outChunkSize);
            ByteBuf buf = Unpooled.copiedBuffer(msg.getBody().outMessageToBytes());
            out.writeBytes(buf.readBytes(toWrite));
            int left = msgLen - toWrite;
            while (left > 0) {
                wrapByFmt((byte)3, header, out);
                toWrite = Math.min(left, outChunkSize);
                out.writeBytes(buf.readBytes(toWrite));
                left -= toWrite;
            }
        }

    }

    private void wrapByFmt(byte fmt, RtmpHeader header, ByteBuf out) {
        out.writeBytes(buildBasicHeader(fmt, header.getChunkStreamId()));
        if (fmt != 3) {
            out.writeBytes(ByteUtil.convertInt2BytesBE(header.getTimeStamp(), 3));
            if (fmt != 2) {
                out.writeBytes(ByteUtil.convertInt2BytesBE(header.getMessageLength(), 3));
                out.writeBytes(ByteUtil.convertInt2BytesBE(header.getMessageTypeId(), 1));
                if (fmt != 1) {
                    out.writeBytes(ByteUtil.convertInt2BytesLE(header.getMessageStreamId(), 4));
                }
            }
        }
    }

    private byte[] buildBasicHeader(byte fmt, int csid) {
        byte first = (byte) (fmt << 6);
        byte[] exCsid = null;
        if (csid <= 63) {
            first ^= csid;
        } else if (csid <= 319) {
            // first 低六位为0      first ^= 0x00
            exCsid = ByteUtil.convertInt2BytesBE(csid - 64, 1);
        } else {
            // first 低六位为1
            first ^= 0x01;
            exCsid = ByteUtil.convertInt2BytesBE(csid - 64, 2);
        }
        return exCsid == null ? new byte[]{first} : ByteUtil.mergeByteArray(new byte[]{first}, exCsid);
    }

}
