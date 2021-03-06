package cn.drelang.live.server.rtmp.entity;

import lombok.Data;

/**
 * RTMP Body
 *
 * @author Drelang
 * @date 2021/3/5 21:59
 */

@Data
public class ChunkBody {
    private byte[] data;
}

