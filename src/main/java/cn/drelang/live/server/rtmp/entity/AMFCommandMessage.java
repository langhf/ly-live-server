package cn.drelang.live.server.rtmp.entity;

import lombok.Data;

import java.util.Map;

/**
 * AMF Command Message consists of command name, transaction ID, and command object.
 * @author Drelang
 * @date 2021/3/6 16:37
 */

@Data
public class AMFCommandMessage {

    private String commandName;

    private Double transactionID;

    private Map<String, Object> objectMap;
}

