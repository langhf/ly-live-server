package cn.drelang.live.server.rtmp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * 处理握手流程
 *
 * 不要用 ByteArrayDecoder，可能存在接收时粘包的现象！！！
 *
 * @author Drelang
 * @date 2021/3/5 19:47
 */
@Slf4j
public class HandShakeDecoder extends ByteToMessageDecoder {

    boolean receivedC0C1;

    boolean handShakeDone;

    static final int C0_LENGTH = 1;
    static final int C1_LENGTH = 1536;

    byte[] handShake = new byte[C1_LENGTH];

    ByteBuf OUT = Unpooled.buffer(4096);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.fireChannelReadComplete();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (handShakeDone) {
            ctx.fireChannelRead(in);
            return ;
        }

        ByteBuf buf = in;
        if (!receivedC0C1) {
            // 第一次进入，buf 的 cap 为 1024，需要等待 buf 扩容
            if (buf.readableBytes() < C0_LENGTH + C1_LENGTH) {
                return ;
            }
            byte C0 = buf.readByte();
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

            OUT.writeByte(S0);
            OUT.writeBytes(S1);
            OUT.writeBytes(S2);
            ctx.writeAndFlush(OUT);
            log.debug("sent S0S1S2");
            handShake = S1;
            receivedC0C1 = true;
        } else {
            byte[] C2 = new byte[C1_LENGTH];
            buf.readBytes(C2, 0, C1_LENGTH);
            if (!bytesEqual(C2, handShake)) {
                log.error("unknown C2 {}", Arrays.toString(C2));
                ctx.close();
            }
            handShake = null;
            // 握手完成后，后续就不需要了握手解码器
            ctx.pipeline().remove(this);
            log.debug("handshake done remoteAddress={}", ctx.channel().remoteAddress().toString());
            handShakeDone = true;
            ctx.flush();
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

