package cn.drelang.live.server.rtmp.handler;

import cn.drelang.live.server.rtmp.entity.*;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import cn.drelang.live.server.rtmp.message.command.netconnection.ConnectMessage;
import cn.drelang.live.server.rtmp.message.protocol.SetChunkSizeMessage;
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
        outs.add(new RtmpMessage(wasMessage.createOutboundHeader(), wasMessage));

        // Set Peer Bandwidth
        SetPeerBandwidthMessage spbMessage = new SetPeerBandwidthMessage(2500_000, (byte) 1);
        outs.add(new RtmpMessage(spbMessage.createOutboundHeader(), spbMessage));

        // 注意：由于 RTMP 默认的 Chunk Size 为 128，而此处想要发送几个 RTMP 消息，总长度超过了 128，
        //      因此要让客户端设置新的 Chunk Size，才能让所有的消息发送过去！

        // Set Chunk Size
        SetChunkSizeMessage scsMessage = new SetChunkSizeMessage(4096);
        outs.add(new RtmpMessage(scsMessage.createOutboundHeader(), scsMessage));

        // _result
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("fmsVer", "FMS/3,0,1,123");
        properties.put("capabilities", 31.0);

        Map<String, Object> information = Maps.newHashMap();
        information.put("code", "NetConnection.Connect.Success");
        information.put("description", "Connection succeeded.");
        information.put("objectEncoding", 0.0);
        information.put("level", "status");

        ConnectMessage outCntMsg = new ConnectMessage();
        outCntMsg.setCommandName("_result");
        outCntMsg.setTransactionID(1.0);
        outCntMsg.setProperties(properties);
        outCntMsg.setInformation(information);

        RtmpHeader header = new RtmpHeader();
        header.setFmt((byte) 0);
        header.setTimeStamp(0);
        header.setMessageLength(outCntMsg.messageToBytes().length);
        header.setMessageTypeId(outCntMsg.outBoundMessageTypeId());
        header.setMessageStreamId(outCntMsg.outboundMsid());
        header.setChannelStreamId(outCntMsg.outboundCsid());

        outs.add(new RtmpMessage(header, outCntMsg));

        ctx.write(outs);
    }

    private void handleCreateStream(ChannelHandlerContext ctx) {

    }


}

