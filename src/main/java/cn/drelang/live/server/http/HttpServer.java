package cn.drelang.live.server.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 *
 * @author Drelang
 * @date 2021/3/4 23:12
 *
 *  ref: https://www.cnblogs.com/demingblog/p/9970772.html
 */
@Slf4j
public class HttpServer {

    public void start(int port) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new HttpServerInitializer());
            ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).sync();
            log.info("http server started on port {}", port);
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("HttpServer start error ", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}

