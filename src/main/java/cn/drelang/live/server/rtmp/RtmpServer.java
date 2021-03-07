package cn.drelang.live.server.rtmp;

import cn.drelang.live.server.rtmp.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 *
 * @author Drelang
 * @date 2021/3/5 19:45
 */
@Slf4j
public class RtmpServer {

    public void start(int port) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(8192));
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new ConnectionHandler())
                                    .addLast(new HandShakeDecoder())
                                    .addLast(new ChunkDecoder())
                                    .addLast(new ChunkEncoder())
                                    .addLast(new CoreRtmpHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).sync();
            log.info("rtmp server started on port {}", port);
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("RtmpServer start error ", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}

