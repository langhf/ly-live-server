package cn.drelang.live.server.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 *
 * @author Drelang
 * @date 2021/3/6 13:41
 */

public class Bean {

    /**
     * 管理推拉流 channel
     */
    public static final Cache<String, String> APP_CHANNEL_KEY;



    private Bean(){}

    static {
        APP_CHANNEL_KEY = CacheBuilder.newBuilder()
                .initialCapacity(10)
                .maximumSize(10000)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build();

    }

}

