package cn.drelang.live;

import cn.drelang.live.server.http.HttpServer;
import cn.drelang.live.server.rtmp.RtmpServer;
import cn.drelang.live.server.test.TestServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Drelang
 * @date 2021/3/4 22:57
 */

public class LyLiveServer {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        readConfig();
        executor.execute(() -> new HttpServer().start(LiveConfig.INSTANCE.getHttpPort()));
        executor.execute(() -> new RtmpServer().start(LiveConfig.INSTANCE.getRtmpPort()));
    }

    private static void readConfig() {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            LiveConfig.INSTANCE =  objectMapper.readValue(new File("config.yaml"), LiveConfig.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

