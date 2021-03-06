package cn.drelang.live.server.rtmp.entity;

import lombok.Data;

/**
 *
 * @author Drelang
 * @date 2021/3/5 22:00
 */

@Data
public class Chunk {

    private ChunkHeader header;

    private ChunkBody body;

    public Chunk(ChunkHeader header, ChunkBody body) {
        this.header = header;
        this.body = body;
    }
}

