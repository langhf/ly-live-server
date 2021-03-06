package cn.drelang.live.server.rtmp.handler;

import cn.drelang.live.LiveConfig;
import cn.drelang.live.server.config.Bean;
import cn.drelang.live.server.exception.OperationNotSupportException;
import cn.drelang.live.server.format.flv.FLV;
import cn.drelang.live.server.format.flv.FLVData;
import cn.drelang.live.server.format.flv.FLVFileBody;
import cn.drelang.live.server.format.flv.FLVTag;
import cn.drelang.live.server.rtmp.entity.*;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import cn.drelang.live.server.rtmp.message.command.DataMessage;
import cn.drelang.live.server.rtmp.message.command.netconnection.ConnectMessage;
import cn.drelang.live.server.rtmp.message.command.netconnection.CreateStreamMessage;
import cn.drelang.live.server.rtmp.message.command.netstream.OnStatusMessage;
import cn.drelang.live.server.rtmp.message.command.netstream.PlayMessage;
import cn.drelang.live.server.rtmp.message.command.netstream.PublishMessage;
import cn.drelang.live.server.rtmp.message.command.netstream.ReleaseStreamMessage;
import cn.drelang.live.server.rtmp.message.media.AudioMessage;
import cn.drelang.live.server.rtmp.message.media.VideoMessage;
import cn.drelang.live.server.rtmp.message.protocol.SetChunkSizeMessage;
import cn.drelang.live.server.rtmp.message.protocol.SetPeerBandwidthMessage;
import cn.drelang.live.server.rtmp.message.protocol.WindowAcknowledgementMessage;
import cn.drelang.live.server.rtmp.message.protocol.userControl.*;
import cn.drelang.live.server.rtmp.stream.Stream;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

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
     * app name
     */
    private String appName;

    /**
     * 上一个 tag 的大小
     */
    private long previousTagSize;

    /**
     * 录制流到该文件
     */
    private FileOutputStream fileOutputStream;

    /**
     * only in publisher's connection
     */
    private Stream publishStream;

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
                case "FCUnpublish":
                    // 不做处理，只处理 deleteStream
                    break;
                case "deleteStream":
                    handleDeleteStream(ctx, msg);
                    break;
                case "play":
                    handlePlay(ctx, msg);
                    break;
            }
        } else if (msid ==  METADATA_AMF0) {
            handleMetaData(ctx, msg);
        } else if (msid == AUDIO_MESSAGE) {
            handleAudioData(ctx, msg);
        } else if (msid == VIDEO_MESSAGE) {
            handleVideoData(ctx, msg);
        } else if (msid == USER_CONTROL_MESSAGE) {
            handleUserControlMessage(ctx, msg);
        } else if (msid == ACKNOWLEDGEMENT_WINDOW_SIZE){
            handleWindowAcknowledgementSize(ctx, msg);
        } else {
            throw new OperationNotSupportException("msid=" + msid);
        }

        if (LiveConfig.INSTANCE.isRecordFlvFile()) {
            if (msid == METADATA_AMF0 || msid == AUDIO_MESSAGE || msid == VIDEO_MESSAGE) {
                saveFile(msg);
            }
        }

        log.debug("handled {}", msg.getBody().getClass().getSimpleName());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
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

        // Set Chunk Size, 此命令含义：告诉对方己方发送 Chunk 的大小，而不是设置对方发送 Chunk 的大小
        SetChunkSizeMessage scsMessage = new SetChunkSizeMessage(128);
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
    }

    private void handleCreateStream(ChannelHandlerContext ctx, RtmpMessage msg) {
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
        ctx.write(out);
    }

    private void handlePublish(ChannelHandlerContext ctx, RtmpMessage msg) {
        PublishMessage publishMessage = (PublishMessage) msg.getBody();

        String channelKey = publishMessage.getPublishingName();
        appName = Bean.APP_CHANNEL_KEY.get(channelKey);

        if (appName == null) {
            log.error("appName={} not exists", appName);
            ctx.close();
            // TODO: not allow to publish
        }

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

        ctx.write(Collections.singletonList(new RtmpMessage(responseHeader, onStatusMessage)));

        publishStream = new Stream();
        publishStream.setAppName(appName);

        Bean.APP_MANAGER.putIfAbsent(appName, publishStream);
    }

    private void handleMetaData(ChannelHandlerContext ctx, RtmpMessage msg) {
        DataMessage dataMessage = (DataMessage) msg.getBody();
        publishStream.setMetaData(dataMessage);
    }

    private void handleVideoData(ChannelHandlerContext ctx, RtmpMessage msg) {
        publishStream.addMedia(msg);
    }

    private void handleAudioData(ChannelHandlerContext ctx, RtmpMessage msg) {
        publishStream.addMedia(msg);
    }

    private void handleDeleteStream(ChannelHandlerContext ctx, RtmpMessage msg) throws IOException {
        ctx.channel().close();

        if (LiveConfig.INSTANCE.isRecordFlvFile() && fileOutputStream != null) {
            FLVFileBody.Node node = new FLVFileBody.Node();
            node.setPreviousTagSize(previousTagSize);
            FLV.encode(fileOutputStream, node);
            fileOutputStream.close();
        }
    }

    private void handleUserControlMessage(ChannelHandlerContext ctx, RtmpMessage msg) {
        UserControlMessage userControlMessage = (UserControlMessage) msg.getBody();
        if (userControlMessage instanceof SetBufferLengthMessage) {

        } else if (userControlMessage instanceof PingRequestMessage) {
            PingResponseMessage pingResponseMessage = new PingResponseMessage();
            pingResponseMessage.setTimeStamp(System.currentTimeMillis());
            ctx.write(Collections.singletonList(pingResponseMessage));
        } else if (userControlMessage instanceof PingResponseMessage) {
            return ;
        }
    }

    private void handlePlay(ChannelHandlerContext ctx, RtmpMessage msg) {
        List<RtmpMessage> out = new ArrayList<>();

        PlayMessage playMessage = (PlayMessage) msg.getBody();

        String appName = playMessage.getStreamName();

        if (!Bean.APP_MANAGER.containsKey(appName)) {
            StreamDryMessage streamDryMessage = new StreamDryMessage();
            streamDryMessage.setStreamId(msg.getHeader().getMessageStreamId());

            RtmpHeader header = UserControlMessage.createOutHeader(streamDryMessage);
            out.add(new RtmpMessage(header, streamDryMessage));
            ctx.write(out);
            ctx.close();
            return ;
        }

        // stream is recorded 1
        StreamIsRecordedMessage streamIsRecordedMessage = new StreamIsRecordedMessage();
        streamIsRecordedMessage.setStreamId(1);
        RtmpHeader header = UserControlMessage.createOutHeader(streamIsRecordedMessage);
        out.add(new RtmpMessage(header, streamIsRecordedMessage));

        // stream begin
        StreamBeginMessage streamBeginMessage = new StreamBeginMessage();
        streamBeginMessage.setStreamId(1);
        RtmpHeader header1 = UserControlMessage.createOutHeader(streamBeginMessage);
        out.add(new RtmpMessage(header1, streamBeginMessage));

        // onStatus - NetStream.Play.Reset
        OnStatusMessage resetMessage = OnStatusMessage.createInstance("status",
                "NetStream.Play.Reset", "Playing and resetting stream.");
        RtmpHeader header2 = OnStatusMessage.createOutHeader(resetMessage);
        out.add(new RtmpMessage(header2, resetMessage));

        // onStatus - NetStream.Play.Start
        OnStatusMessage playStartMessage = OnStatusMessage.createInstance("status",
                "NetStream.Play.Start", "Started playing stream.");
        RtmpHeader header3 = OnStatusMessage.createOutHeader(playStartMessage);
        out.add(new RtmpMessage(header3, playStartMessage));

        // onStatus - NetStream.Data.Start
        OnStatusMessage dataStartMessage = OnStatusMessage.createInstance("status",
                "NetStream.Data.Start", "Started playing stream.");
        RtmpHeader header4 = OnStatusMessage.createOutHeader(dataStartMessage);
        out.add(new RtmpMessage(header4, dataStartMessage));

        Stream stream = Bean.APP_MANAGER.get(appName);
        // onMetaData
        DataMessage onMetaData = new DataMessage();
        onMetaData.setDesc("onMetaData");
        onMetaData.setEcmaArray(stream.getMetaData().getEcmaArray());
        RtmpHeader header5 = DataMessage.createOutHeader(onMetaData);
        out.add(new RtmpMessage(header5, onMetaData));
        ctx.write(out);

        // add this channel to Stream
        stream.addSubscriber(ctx.channel());

    }

    private void handleWindowAcknowledgementSize(ChannelHandlerContext ctx, RtmpMessage msg) {
        // TODO: 待实现
        return ;
    }

    private void saveFile(RtmpMessage msg) throws IOException {
        if (fileOutputStream == null) {
            fileOutputStream = new FileOutputStream(appName + "_" + System.currentTimeMillis() +".flv");
            FLV.encodeFLVHeader(fileOutputStream);
        }

        RtmpHeader header = msg.getHeader();
        RtmpBody body = msg.getBody();

        FLVTag.TAG_TYPE tag_type;
        byte[] tagData;
        if (body instanceof AudioMessage) {
            tagData = body.outMessageToBytes();
            tag_type = FLVTag.TAG_TYPE.AUDIO;
        } else if (body instanceof VideoMessage) {
            tagData = body.outMessageToBytes();
            tag_type = FLVTag.TAG_TYPE.VIDEO;
        } else if (body instanceof DataMessage) {
            FLVData.Script script = new FLVData.Script();
            FLVData.Script.ScriptData scriptData = new FLVData.Script.ScriptData();
            scriptData.setObjectName(((DataMessage) body).getDesc());
            scriptData.setObjectData(((DataMessage) body).getEcmaArray());
            script.setObjects(Collections.singletonList(scriptData));
            tagData = FLV.encodeScript(script);
            tag_type = FLVTag.TAG_TYPE.SCRIPT;
        } else {
            log.error("unsupported msg " + msg);
            return ;
        }

        FLVTag tag = new FLVTag();
        tag.setType(tag_type);
        tag.setDataSize(tagData.length);
        tag.setTimeStamp(header.getTimeStamp());
        tag.setTimeStampExtended((byte) 0);
        tag.setStreamId(0);
//        tag.setData(data);  // 此处不赋值 data，避免重复 encode，提高效率

        FLVFileBody.Node node = new FLVFileBody.Node();
        node.setPreviousTagSize(previousTagSize);
        node.setTag(tag);
        FLV.encode(fileOutputStream, node, tagData);

        previousTagSize = tagData.length + 11;
    }

}

