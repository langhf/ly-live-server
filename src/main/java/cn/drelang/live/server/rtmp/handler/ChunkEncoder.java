package cn.drelang.live.server.rtmp.handler;

import cn.drelang.live.server.rtmp.entity.RtmpBody;
import cn.drelang.live.server.rtmp.entity.RtmpHeader;
import cn.drelang.live.server.rtmp.entity.RtmpMessage;
import cn.drelang.live.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 *
 * @author Drelang
 * @date 2021/3/6 14:58
 */

@Slf4j
public class ChunkEncoder extends MessageToByteEncoder<List<RtmpMessage>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, List<RtmpMessage> messages, ByteBuf out) throws Exception {
        messages.forEach(msg-> wrapMessage(msg, out));
        ctx.writeAndFlush(out);
    }

    private void wrapMessage(RtmpMessage msg, ByteBuf out) {
        RtmpHeader header = msg.getHeader();

        // wrap header
        byte fmt = header.getFmt();
        byte first = (byte) (fmt << 6);
        int csid = header.getChannelStreamId();
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

        out.writeByte(first);
        if (exCsid != null) {
            out.writeBytes(exCsid);
        }

        if (fmt != 3) {
            out.writeBytes(ByteUtil.convertInt2BytesBE(header.getTimeStamp(), 3));
            if (fmt != 2) {
                out.writeBytes(ByteUtil.convertInt2BytesBE(header.getMessageLength(), 3));
                out.writeBytes(ByteUtil.convertInt2BytesBE(header.getMessageTypeId(), 1));
                if (fmt != 1) {
                    out.writeBytes(ByteUtil.convertInt2BytesBE(header.getMessageStreamId(), 4));
                }
            }
        }

        // wrap body
        out.writeBytes(msg.getBody().messageToBytes());
    }

}

