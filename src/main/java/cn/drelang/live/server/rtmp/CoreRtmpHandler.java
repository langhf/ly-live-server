package cn.drelang.live.server.rtmp;

import cn.drelang.live.server.rtmp.entity.*;
import cn.drelang.live.server.rtmp.message.CommandMessage;
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
//    private void handleConnect(ChannelHandlerContext ctx) {
//        List<RtmpMessage> outs = Lists.newArrayList();
//        // Window Acknowledgement Size
//        RtmpHeader wasHeader = new RtmpHeader();
//        wasHeader.setFmt((byte) 0);
//        wasHeader.setChannelStreamId(PROTOCOL_CONTROL_MESSAGE_CSID);
//        wasHeader.setMessageStreamId(PROTOCOL_CONTROL_MESSAGE_MSID);
//        wasHeader.setMessageTypeId(ACKNOWLEDGEMENT_WINDOW_SIZE);
//        wasHeader.setTimeStamp(0);
//        wasHeader.setMessageLength(4);
//
//        RtmpBody wasBody = new RtmpBody();
//        wasBody.setData(ByteUtil.convertInt2BytesBE(2500_000, 4));
//
//        outs.add(new RtmpMessage(wasHeader, wasBody));
//
//        // Set Peer Bandwidth
//        RtmpHeader spbHeader = new RtmpHeader();
//        spbHeader.setFmt((byte)0);
//        spbHeader.setChannelStreamId(PROTOCOL_CONTROL_MESSAGE_CSID);
//        spbHeader.setMessageStreamId(PROTOCOL_CONTROL_MESSAGE_MSID);
//        spbHeader.setMessageTypeId(SET_PEER_BANDWIDTH);
//        spbHeader.setTimeStamp(0);
//        spbHeader.setMessageLength(5);
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
//
//        ctx.write(outs);
//    }

    private void handleCreateStream(ChannelHandlerContext ctx) {

    }


}

