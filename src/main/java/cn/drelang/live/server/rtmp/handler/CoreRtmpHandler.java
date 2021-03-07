package cn.drelang.live.server.rtmp.handler;

import cn.drelang.live.server.rtmp.entity.*;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import cn.drelang.live.server.rtmp.message.protocol.SetPeerBandwidthMessage;
import cn.drelang.live.server.rtmp.message.protocol.WindowAcknowledgementMessage;
import cn.drelang.live.util.ByteUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static cn.drelang.live.server.rtmp.entity.Constants.*;

/**
 *
 * @author Drelang
 * @date 2021/3/5 19:47
 */

@Slf4j
public class CoreRtmpHandler extends SimpleChannelInboundHandler<RtmpMessage> {

    ByteBuf TEMP = Unpooled.buffer(1024);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RtmpMessage msg) throws Exception {
        RtmpHeader header = msg.getHeader();
        switch (header.getMessageTypeId()) {
            case COMMAND_MESSAGE_AMF0:
                CommandMessage commandMessage = (CommandMessage) msg.getBody();
                String commandName = commandMessage.getCommandName();
                if (commandName.equals("connect")) {
                    handleConnect(ctx);
                }
//                AMFCommandMessage commandMessage = AMF0.decodeCommandMessage(msg.getBody().getData());
//                handleByCommand(ctx, commandMessage);
        }

        System.out.println("ha");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("CoreRtmpHandler error ", cause);
    }

//    private void handleByCommand(ChannelHandlerContext ctx, CommandMessage commandMessage) {
//        log.debug("handleByCommand {}", commandMessage);
//        switch (commandMessage.getCommandName()) {
//            case "connect":
//                handleConnect(ctx);
//            case "createStream":
//                handleCreateStream(ctx);
//        }
//
//    }
//
    private void handleConnect(ChannelHandlerContext ctx) {
        List<RtmpMessage> outs = Lists.newArrayList();
        // Window Acknowledgement Size
        WindowAcknowledgementMessage wasMessage = new WindowAcknowledgementMessage(2500_000);
        RtmpHeader wasHeader = RtmpHeader.createOutHeaderByMessage(wasMessage);

        outs.add(new RtmpMessage(wasHeader, wasMessage));

        // Set Peer Bandwidth
        SetPeerBandwidthMessage spbMessage = new SetPeerBandwidthMessage(2500_000, (byte) 1);
        RtmpHeader spbHeader = RtmpHeader.createOutHeaderByMessage(spbMessage);
        outs.add(new RtmpMessage(spbHeader, spbMessage));
//
//        TEMP.writeBytes(ByteUtil.convertInt2BytesBE(2500_000, 4));
//        TEMP.writeByte(0x01);
//        byte[] spb = new byte[TEMP.readableBytes()];
//        TEMP.readBytes(spb);
//        RtmpBody spbBody = new RtmpBody(spb);
//
//        outs.add(new RtmpMessage(spbHeader, spbBody));
//
//        // _result
//        String command = "_result";
//        double transactionId = 1.0;
//        Map<String, String> objectMap = Maps.newHashMap();
////        objectMap
//        RtmpHeader resultHeader = new RtmpHeader();

        ctx.write(outs);
    }

    private void handleCreateStream(ChannelHandlerContext ctx) {

    }


}

