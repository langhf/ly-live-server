package cn.drelang.live.server.rtmp.message.command;

import io.netty.buffer.ByteBuf;

/**
 * 继承 CommandMessage 类型必须要实现的接口
 *
 * @author Drelang
 * @date 2021/3/8 00:19
 */

public interface CommandMessageInterface<T extends CommandMessage> {
    /**
     * 拷贝 commandMessage
     */
    T create(CommandMessage commandMessage, ByteBuf in);
}

