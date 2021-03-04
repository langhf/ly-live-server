package cn.drelang.live;

import cn.drelang.live.server.http.HttpServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;

/**
 *
 * @author Drelang
 * @date 2021/3/4 22:57
 */

public class LyLiveServer {
    public static void main(String[] args) {
        readConfig();
        new HttpServer().start(LiveConfig.INSTANCE.getHttpPort());
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

