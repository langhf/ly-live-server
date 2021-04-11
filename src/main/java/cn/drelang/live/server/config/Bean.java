package cn.drelang.live.server.config;

import cn.drelang.live.server.rtmp.stream.Stream;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Drelang
 * @date 2021/3/6 13:41
 */

public class Bean {

    /**
     * 管理推拉流 channel
     */
    public static final HashMap<String, String> APP_CHANNEL_KEY;

    /**
     * manage app stream
     * example: movie -> stream1,  tv -> stream2
     */
    public static final HashMap<String, Stream> APP_MANAGER;

    private Bean(){}

    static {
        APP_CHANNEL_KEY = new HashMap<>(16);
        APP_MANAGER = new HashMap<>(16);
    }

}

