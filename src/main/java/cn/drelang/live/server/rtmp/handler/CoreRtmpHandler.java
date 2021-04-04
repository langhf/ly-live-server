package cn.drelang.live.server.rtmp.handler;

import cn.drelang.live.server.exception.OperationNotSupportException;
import cn.drelang.live.server.rtmp.amf.ECMAArray;
import cn.drelang.live.server.rtmp.entity.*;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import cn.drelang.live.server.rtmp.message.command.DataMessage;
import cn.drelang.live.server.rtmp.message.command.netconnection.ConnectMessage;
import cn.drelang.live.server.rtmp.message.command.netconnection.CreateStreamMessage;
import cn.drelang.live.server.rtmp.message.command.netstream.OnStatusMessage;
import cn.drelang.live.server.rtmp.message.command.netstream.PublishMessage;
import cn.drelang.live.server.rtmp.message.command.netstream.ReleaseStreamMessage;
import cn.drelang.live.server.rtmp.message.protocol.SetChunkSizeMessage;
import cn.drelang.live.server.rtmp.message.protocol.SetPeerBandwidthMessage;
import cn.drelang.live.server.rtmp.message.protocol.WindowAcknowledgementMessage;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static cn.drelang.live.server.rtmp.entity.Constants.*;

/**
 * 核心处理逻辑
 *
 * @author Drelang
 * @date 2021/3/5 19:47
 */

@Slf4j
public class CoreRtmpHandler extends SimpleChannelInboundHandler<RtmpMessage> {

    /**
     * releaseStream 命令是否通过
     */
    private boolean releaseStreamOK;

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        try {
//            RtmpMessage m = (RtmpMessage) msg;
//            channelRead0(ctx, m);
//        } catch (Exception e) {
//            log.error("error", e);
//        }
//    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.flush();
        // do nothing
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RtmpMessage msg) throws Exception {
        RtmpHeader header = msg.getHeader();
        byte msid = header.getMessageTypeId();
        if (msid == COMMAND_MESSAGE_AMF0) {
            CommandMessage commandMessage = (CommandMessage) msg.getBody();
            String commandName = commandMessage.getCommandName();
            switch (commandName) {
                case "connect":
                    handleConnect(ctx);
                    break;
                case "releaseStream":
                    handleReleaseStream(ctx, msg);
                    break;
                case "createStream":
                    handleCreateStream(ctx, msg);
                    break;
                case "publish":
                    handlePublish(ctx, msg);
                    break;
            }
        } else if (msid ==  METADATA_AMF0) {
            handleMetaData(ctx, msg);
        } else if (msid == AUDIO_MESSAGE) {

        } else if (msid == VIDEO_MESSAGE) {

        } else {
            throw new OperationNotSupportException("msid=" + msid);
        }

        System.out.println(msg.getBody().getClass().getSimpleName());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("CoreRtmpHandler error ", cause);
    }

    private void handleConnect(ChannelHandlerContext ctx) {
        List<RtmpMessage> outs = new ArrayList<>(4);
        // Window Acknowledgement Size
        WindowAcknowledgementMessage wasMessage = new WindowAcknowledgementMessage(2500_000);
        outs.add(new RtmpMessage(wasMessage.createOutboundHeader(), wasMessage));

        // Set Peer Bandwidth
        SetPeerBandwidthMessage spbMessage = new SetPeerBandwidthMessage(2500_000, (byte) 1);
        outs.add(new RtmpMessage(spbMessage.createOutboundHeader(), spbMessage));

        // 注意：由于 RTMP 默认的 Chunk Size 为 128，而此处想要发送几个 RTMP 消息，总长度超过了 128，
        //      因此要让客户端设置新的 Chunk Size，才能让所有的消息发送过去！

        // Set Chunk Size, 此命令含义：告诉对方己方发送 Chunk 的大小，而不是设置对方发送 Chunk 的大小
        SetChunkSizeMessage scsMessage = new SetChunkSizeMessage(1024);
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
        header.setMessageLength(outCntMsg.outMessageToBytes().length);
        header.setMessageTypeId(outCntMsg.outBoundMessageTypeId());
        header.setMessageStreamId(outCntMsg.outboundMsid());
        header.setChunkStreamId(outCntMsg.outboundCsid());

        outs.add(new RtmpMessage(header, outCntMsg));

        ctx.write(outs);
    }

    private void handleReleaseStream(ChannelHandlerContext ctx, RtmpMessage msg) {
        ReleaseStreamMessage releaseStreamMessage = (ReleaseStreamMessage) msg.getBody();
        String channelKey = releaseStreamMessage.getChannelKey();
        if (channelKey == null || channelKey.equals("")) {
            log.error("handler releaseStream error channelKey={}", channelKey);
            ctx.close();
            return ;
        }
        // TODO: 检查 channelKey 是否合法
        releaseStreamOK = true;
    }

    private void handleCreateStream(ChannelHandlerContext ctx, RtmpMessage msg) {
        if (!releaseStreamOK) {
            ctx.close();
            return ;
        }

        CreateStreamMessage request = (CreateStreamMessage) msg.getBody();

        CreateStreamMessage response = new CreateStreamMessage();
        response.setCommandName("_result");
        response.setTransactionID(request.getTransactionID());
        response.setOutCommandObject(null);
        response.setOutStreamId(1);

        RtmpHeader responseHeader = new RtmpHeader();
        responseHeader.setFmt((byte) 0);
        responseHeader.setChunkStreamId(3);
        responseHeader.setTimeStamp(0);
        responseHeader.setMessageTypeId(response.outBoundMessageTypeId());
        responseHeader.setMessageStreamId(response.outboundMsid());
        responseHeader.setMessageLength(response.outMessageToBytes().length);

        List<RtmpMessage> out = new ArrayList<>(1);
        out.add(new RtmpMessage(responseHeader, response));
//        ByteBuf buf = ctx.alloc().buffer();
        ctx.write(out);
//        ReferenceCountUtil.release(out);
    }

    private void handlePublish(ChannelHandlerContext ctx, RtmpMessage msg) {
        PublishMessage publishMessage = (PublishMessage) msg.getBody();

        Map<String, Object> infoObject = new HashMap<>(4);
        infoObject.put("level", "status");
        infoObject.put("code", "NetStream.Publish.Start");
        infoObject.put("description", "start publishing");

        OnStatusMessage onStatusMessage = new OnStatusMessage();
        onStatusMessage.setCommandObject(null);
        onStatusMessage.setInfoObject(infoObject);

        RtmpHeader responseHeader = new RtmpHeader();
        responseHeader.setFmt((byte) 0);
        responseHeader.setChunkStreamId(onStatusMessage.outboundCsid());
        responseHeader.setTimeStamp(0);
        responseHeader.setMessageTypeId(onStatusMessage.outBoundMessageTypeId());
        responseHeader.setMessageStreamId(onStatusMessage.outboundMsid());
        responseHeader.setMessageLength(onStatusMessage.outMessageToBytes().length);

        ctx.write(Arrays.asList(new RtmpMessage(responseHeader, onStatusMessage)));
    }

    private void handleMetaData(ChannelHandlerContext ctx, RtmpMessage msg) {
        DataMessage dataMessage = (DataMessage) msg.getBody();
        ECMAArray ecmaArray = dataMessage.getEcmaArray();
    }

    private void handleVideoData(ChannelHandlerContext ctx, RtmpMessage msg) {

    }

}

