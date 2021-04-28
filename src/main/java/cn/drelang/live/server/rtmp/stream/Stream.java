package cn.drelang.live.server.rtmp.stream;

import cn.drelang.live.server.rtmp.entity.RtmpHeader;
import cn.drelang.live.server.rtmp.entity.RtmpMessage;
import cn.drelang.live.server.rtmp.message.command.DataMessage;
import cn.drelang.live.server.rtmp.message.media.AudioMessage;
import cn.drelang.live.server.rtmp.message.media.MediaMessage;
import cn.drelang.live.server.rtmp.message.media.VideoMessage;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * a live stream. Publisher publish media data into mediaCache, Subcribers play media data through mediaCache on the very first time.
 *
 * @author Drelang
 * @date 2021/4/5 12:07
 */

@Slf4j
public class Stream {

    /**
     * live app name
     */
    String appName;

    /**
     * Stream's metadata
     */
    DataMessage metaData;

    /**
     * key frame with head, send to client in the very first time
     */
    VideoMessage keyFrameHead;

    /**
     * audio with head, send to client in the very first time
     */
    AudioMessage audioHead;

    /**
     * most recent video data (include key frame) and audio data
     */
    ConcurrentLinkedDeque<MediaMessage> mediaCache;

    /**
     * subscribers, may include inactive channel
     */
    ConcurrentLinkedQueue<Channel> subscribers;

    Map<Channel, Long> timeMap;

    public Stream() {
        this.mediaCache = new ConcurrentLinkedDeque<>();
        this.subscribers = new ConcurrentLinkedQueue<>();
        this.timeMap = new HashMap<>();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public DataMessage getMetaData() {
        return metaData;
    }

    public void setMetaData(DataMessage metaData) {
        this.metaData = metaData;
    }

    /**
     * add a new subscriber
     * @param channel subscriber's channel
     */
    public void addSubscriber(Channel channel) {
        log.info("add subscriber {}", channel);
        subscribers.add(channel);
        timeMap.put(channel, System.currentTimeMillis());

        if (null == keyFrameHead || null == audioHead) {
            return ;
        }

        int timestamp = getTimeStamp(channel);
        RtmpHeader videoHeader = keyFrameHead.creatOutHeader(timestamp);
        RtmpHeader audioHeader = audioHead.creatOutHeader(timestamp);

        List<RtmpMessage> out = new ArrayList<>();
        out.add(new RtmpMessage(videoHeader, keyFrameHead));
        out.add(new RtmpMessage(audioHeader, audioHead));
        channel.write(out);
        log.info("SEND_MEDIA_HEAD channel={}, media={}", channel, out);

        mediaCache.forEach(msg -> {
            channel.write(Collections.singletonList(msg));
            log.info("SEND_CACHE_MEDIA channel={}, meida={}", channel, msg);
        });
    }

    private int getTimeStamp(Channel channel) {
        return (int) (System.currentTimeMillis() - timeMap.get(channel));
    }

    /**
     * in this channel, play the first time
     * @param channel subscriber
     */
    public synchronized void playFirst(Channel channel) {
        List<MediaMessage> mediaMessages = new ArrayList<>(mediaCache);
        channel.write(mediaMessages);
    }

    public void addMedia(RtmpMessage message) {
        MediaMessage body = (MediaMessage) message.getBody();

        RtmpHeader outHeader;
        if (body instanceof VideoMessage) {
            VideoMessage vm = (VideoMessage) body;
            if (vm.isKeyFrameHead()) {
                log.info("find key frame head {}", vm);
                keyFrameHead = vm;
            }
            if (vm.isKeyFrame()) {
                log.info("find key frame");
                mediaCache.clear();
            }
        } else if (body instanceof AudioMessage) {
            AudioMessage am = (AudioMessage) body;
            if (am.isAACHead()) {
                audioHead = am;
            }
        } else {
            throw new RuntimeException("unsupported media message " + message);
        }
        mediaCache.add(body);
        broadcastToSubscribers(body);
    }

    public synchronized void broadcastToSubscribers(MediaMessage message) {
        Iterator<Channel> subs = subscribers.iterator();
        RtmpHeader header = null;
        while (subs.hasNext()) {
            Channel channel = subs.next();
            if (!channel.isActive()) {
                subs.remove();
            } else {
                if (message instanceof VideoMessage) {
                    header = ((VideoMessage) message).creatOutHeader(getTimeStamp(channel));
                } else if (message instanceof AudioMessage) {
                    header = ((AudioMessage) message).creatOutHeader(getTimeStamp(channel));
                } else {
                    continue;
                }
                RtmpMessage msg = new RtmpMessage(header, message);
                channel.write(Collections.singletonList(msg));
                log.info("SEND_MEDIA channel={},  media={}", channel, msg);
            }
        }
    }



}

