package cn.drelang.live.server.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 *
 * @author Drelang
 * @date 2021/3/4 23:22
 *
 */

public class HttpServerInitializer extends ChannelInitializer {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast("httpAggregator",new HttpObjectAggregator(512*1024)); // http 消息聚合器
        pipeline.addLast("coreHttpHandler", new CoreHttpHandler());
    }
}

