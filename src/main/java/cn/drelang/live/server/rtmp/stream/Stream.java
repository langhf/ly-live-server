package cn.drelang.live.server.rtmp.stream;

import cn.drelang.live.server.rtmp.message.command.DataMessage;
import cn.drelang.live.server.rtmp.message.media.MediaMessage;
import cn.drelang.live.server.rtmp.message.media.VideoMessage;
import io.netty.channel.Channel;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * a live stream. Publisher publish media data into mediaCache, Subcribers play media data through mediaCache on the very first time.
 *
 * @author Drelang
 * @date 2021/4/5 12:07
 */

@Data
public class Stream {

    /**
     * live app name
     */
    private String appName;

    /**
     * most recent video data (include key frame) and audio data
     */
    private ConcurrentLinkedQueue<MediaMessage> mediaCache;

    /**
     * Stream's metadata
     */
    private DataMessage metaData;

    /**
     * subcribers, may include inactive channel
     */
    private ConcurrentLinkedQueue<Channel> subscribers;

    /**
     * add a new subscriber
     * @param channel subscriber's channel
     */
    public void addSubscriber(Channel channel) {
        subscribers.add(channel);
    }

    /**
     * in this channel, play the first time
     * @param channel subcriber
     */
    public void playFirst(Channel channel) {
        List<MediaMessage> mediaMessages = new ArrayList<>(mediaCache);
        channel.writeAndFlush(mediaMessages);
    }

    public void addMedia(MediaMessage message) {
        if (message instanceof VideoMessage) {
            VideoMessage vm = (VideoMessage) message;
            if (vm.isKeyFrame()) {
                mediaCache.clear();
            }
        }
        mediaCache.add(message);
        broadcastToSubcribers(message);
    }

    public void broadcastToSubcribers(MediaMessage message) {
        Iterator<Channel> subs = subscribers.iterator();
        while (subs.hasNext()) {
            Channel channel = subs.next();
            if (!channel.isActive()) {
                subs.remove();
            } else {
                channel.writeAndFlush(Collections.singletonList(message));
            }
        }
    }



}

