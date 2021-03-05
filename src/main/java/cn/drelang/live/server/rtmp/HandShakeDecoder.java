package cn.drelang.live.server.rtmp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * 处理握手流程
 *
 * @author Drelang
 * @date 2021/3/5 19:47
 */
@Slf4j
public class HandShakeDecoder extends ByteArrayDecoder {

    boolean receivedC0C1;

    boolean handShakeDone;

    static final int C0_LENGTH = 1;
    static final int C1_LENGTH = 1536;

    byte[] handShake = new byte[C1_LENGTH];

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().flush();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        ByteBuf buf = in;
        if (!receivedC0C1) {
            byte C0 = in.readByte();
            if (C0 != 0x03) {
                ctx.close();
                return ;
            }
            buf.readBytes(handShake, 0, C1_LENGTH);
            log.debug("received C0C1 from remoteAddress={}", ctx.channel().remoteAddress().toString());
            // send S0, S1, S2
            byte[] S2 = handShake;
            byte[] S1 = new byte[C1_LENGTH];
            byte S0 = C0;

            ByteBufAllocator allocator = ctx.alloc();

            ctx.write(allocator.buffer(C0_LENGTH).writeByte(S0));
            ctx.write(allocator.buffer(C1_LENGTH).writeBytes(S1));
            ctx.writeAndFlush(allocator.buffer(C1_LENGTH).writeBytes(S2));
            log.debug("sent S0S1S2");
            handShake = S1;
            receivedC0C1 = true;
        } else {
            byte[] C2 = new byte[C1_LENGTH];
            if (!bytesEqual(C2, handShake)) {
                log.error("unknown C2 {}", Arrays.toString(C2));
                ctx.close();
            }
            handShake = null;
            // 握手完成后，后续就不需要了握手解码器
            ctx.pipeline().remove(this);
            log.debug("handshake done remoteAddress={}", ctx.channel().remoteAddress().toString());
            handShakeDone = true;
        }
    }

    private boolean bytesEqual(byte[] b1, byte[] b2) {
        if (b1 == null || b2 == null || b1.length != b2.length) {
            return false;
        }

        for (int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("handshake error ", cause);
    }
}

