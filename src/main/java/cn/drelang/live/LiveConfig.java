package cn.drelang.live;

import lombok.Data;

/**
 *
 * @author Drelang
 * @date 2021/3/4 22:57
 */

@Data
public class LiveConfig {

    public static LiveConfig INSTANCE;

    /**
     * HTTP 服务端口
     */
    private int httpPort;

    /**
     * RTMP 服务端口
     */
    private int rtmpPort;

    /**
     * 是否录制
     */
    private boolean recordFlvFile;

    /**
     * 录制路径
     */
    private String recordPath;
}

