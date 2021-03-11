package cn.drelang.live.server.rtmp.message.command.netstream;

import cn.drelang.live.server.rtmp.amf.AMF0;
import io.netty.buffer.ByteBuf;

/**
 * 发布流
 *
 * @author Drelang
 * @date 2021/3/11 22:25
 */

public class PublishMessage extends NetStreamC2SMessage {

    /**
     * 永远是 null，占位
     */
    private Object commandObject;

    /**
     * Name with which the stream is published
     */
    private String publishingName;

    /**
     * Type of publishing. Set to "live", "record", or "append".
     *
     *   record: The stream is published and the data is recorded to a new file.
     *      The file is stored on the server in a subdirectory within the directory
     *      that contains the server application. If the file already exists, it is overwritten.
     *
     *   append: The stream is published and the data is appended to a file. If no file is found, it is created.
     *
     *   live: Live data is published without recording it in a file.
     */
    private String publishingType;

    public Object getCommandObject() {
        return commandObject;
    }

    public String getPublishingName() {
        return publishingName;
    }

    public String getPublishingType() {
        return publishingType;
    }

    @Override
    public String toReadableString() {
        return null;
    }

    @Override
    public void continueDecode(ByteBuf in) {
        commandObject = AMF0.decodeAMF0Type(in);
        publishingName = (String) AMF0.decodeAMF0Type(in);
        publishingType = (String) AMF0.decodeAMF0Type(in);
    }
}

