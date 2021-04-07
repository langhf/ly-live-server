package cn.drelang.live.server.http;

import cn.drelang.live.server.config.Bean;
import cn.drelang.live.util.ResponseUtil;
import com.google.common.collect.Maps;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 *
 * @author Drelang
 * @date 2021/3/4 23:24
 */
@Slf4j
public class CoreHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String URI_SEP = "\\?";
    private static final String PARAM_INTER_SEP = "&";
    private static final String PARAM_INNER_SEP = "=";
    private static final String HASH_SALT = "ytoahajfgla750198349fjlsajdxvjzcxla-sawlfjalskjdf";

    /**
     * key: uri path
     * value: function
     */
    private static final Map<String, BiFunction<ChannelHandlerContext, FullHttpRequest, FullHttpResponse>> uriActions;

    private static final HashFunction HASH_FUNCTION = Hashing.sha256();

    static {
        uriActions = Maps.newHashMap();
        uriActions.put("/channel/get", CoreHttpHandler::getChannelKey);
        uriActions.put("/favicon.ico", CoreHttpHandler::handleFaviconIcon);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        //100 Continue
        if (HttpUtil.is100ContinueExpected(request)) {
            ctx.write(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.CONTINUE));
        }

        String uri = request.uri().split(URI_SEP)[0];

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

    private static FullHttpResponse getChannelKey(ChannelHandlerContext ctx, FullHttpRequest request) {
        String[] ss = request.uri().split(URI_SEP);
        if (ss.length < 2) {
            return composeHttpRes(ResponseUtil.build(400, "need param like app=movie"));
        }

        Map<String, String> params = parseParams(ss[1]);
        String appName = params.get("app");
        String channelKey = Bean.APP_CHANNEL_KEY.getIfPresent(appName);


        if (channelKey == null) {
//            return composeHttpRes(ResponseUtil.build(400, "this app has been used by other"));
            channelKey = HASH_FUNCTION.hashString(appName, StandardCharsets.UTF_8).toString();
            Bean.APP_CHANNEL_KEY.put(appName, channelKey);
            Bean.APP_CHANNEL_KEY.put(channelKey, appName);
        }

        Map<String, Object> data = new HashMap<>(1);
        data.put("appName", appName);
        data.put("channelKey", channelKey);
        return composeHttpRes(ResponseUtil.build(RetCodeEnum.OK, data));
    }

    private static FullHttpResponse handleFaviconIcon(ChannelHandlerContext ctx, FullHttpRequest request) {
        return composeHttpRes(ResponseUtil.build(RetCodeEnum.NOT_FOUNT));
    }

    private static Map<String, String> parseParams(String param) {
        Map<String, String> res = new HashMap<>();
        if (param == null || param.length() == 0) {
            return res;
        }

        String[] ss = param.split(PARAM_INTER_SEP);
        for (String s : ss) {
            String[] t = s.split(PARAM_INNER_SEP);
            if (t.length != 2) {
                continue;
            }
            res.put(t[0], t[1]);
        }
        return res;
    }

    private static FullHttpResponse composeHttpRes(String msg) {
        return new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
    }

    private static FullHttpResponse composeHttpRes(BaseResponse<?> msg) {
        return new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(msg.toJsonString(), CharsetUtil.UTF_8));
    }

}

