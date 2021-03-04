package cn.drelang.live.server.http;

import cn.drelang.live.util.ResponseUtil;
import com.google.common.collect.Maps;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.BiFunction;

/**
 *
 * @author Drelang
 * @date 2021/3/4 23:24
 */
@Slf4j
public class CoreHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Map<String, BiFunction<ChannelHandlerContext, FullHttpRequest, FullHttpResponse>> uriActions;

    static {
        uriActions = Maps.newHashMap();
        uriActions.put("/channel/get", CoreHttpHandler::getChannelKey);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        //100 Continue
        if (HttpUtil.is100ContinueExpected(request)) {
            ctx.write(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.CONTINUE));
        }

        String uri = request.uri();
        BiFunction<ChannelHandlerContext, FullHttpRequest, FullHttpResponse> action = uriActions.get(uri);
        if (action == null) {
            log.error("unable to find action uri={}", uri);
            ctx.writeAndFlush(ResponseUtil.build(RetCodeEnum.NOT_FOUNT).toJsonString()).addListener(ChannelFutureListener.CLOSE);
            return ;
        }

        // 设置头信息
        FullHttpResponse response = action.apply(ctx, request);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

    private static FullHttpResponse composeHttpRes(String msg) {
        return new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
    }

    private static FullHttpResponse getChannelKey(ChannelHandlerContext ctx, FullHttpRequest request) {
        Map<String, Object> data = Maps.newHashMap();
        data.put("channel", "hello-afda-fas");
        return composeHttpRes(ResponseUtil.build(RetCodeEnum.OK, data).toJsonString());
    }
}

