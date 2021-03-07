package cn.drelang.live.server.rtmp.message.command;

import cn.drelang.live.server.rtmp.entity.RtmpBody;

/**
 *  Rtmp Command Message 主要有以下，括号内为 Message Type Id：
 *   - Command Message (20, 17)
 *   - User Control Message (4)
 *   - Data Message (18, 15)
 *   - Shared Object Message (19, 16)
 *   - Audio Message (8)
 *   - Video Message (9)
 *   - Aggregate Message (22)
 *
 * @author Drelang
 * @date 2021/3/7 18:54
 */

public abstract class RtmpCommandMessage implements RtmpBody {
    public abstract byte outMessageTypeId();
}

