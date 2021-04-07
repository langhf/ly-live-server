package cn.drelang.live.server.rtmp.message.command.netstream;

import cn.drelang.live.server.exception.OperationNotSupportException;
import cn.drelang.live.server.rtmp.amf.AMF0;
import cn.drelang.live.server.rtmp.message.command.CommandMessage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 *
 * @author Drelang
 * @date 2021/4/8 00:20
 */

@Data
public class PlayMessage extends CommandMessage {

    /**
     * mark null
     */
    private Object commandObject;

    /**
     * Name of the stream to play. To play video (FLV) files, specify the name of the stream without a file
     * extension (for example, "sample"). To play back MP3 or ID3 tags, you must precede the stream name with
     * mp3: (for example, "mp3:sample". To play H.264/AAC files, you must precede the stream name with mp4:
     * and specify the file extension. For example, to play the file sample.m4v,specify "mp4:sample.m4v"
     */
    private String streamName;

    /**
     * An optional parameter that specifies the start time in seconds. The default value is -2, which means the
     * subscriber first tries to play the live stream specified in the Stream Name field. If alive stream of that
     * name is not found,itplays the recorded stream of the same name. If there is no recorded stream with that name,
     * the subscriber waits fora new live stream with that name and plays it when available. If you pass -1 in the
     * com.sun.tools.javadoc.Start field, only the live stream specified in the Stream Name field is played. If you
     * pass 0 or a positive number in the Start field, a recorded stream specified in the Stream Name field is played
     * beginning from the time specified in the Start field. If no recorded stream is found, the next item in the playlist is played.
     */
    private Double start;

    /**
     * An optional parameter that specifies the duration of playback in seconds. The default value is -1.
     * The -1 value means a live stream is played until it is no longer available or a recorded stream is
     * played until it ends. If you pass 0, it plays the single frame since the time specified in the Start
     * field from the beginning of a recorded stream. It is assumed that the value specified in the Start
     * field is equal to or greater than 0. If you pass a positive number,  it plays a live stream for
     * the time period specified in the Duration field. After that it becomes available or plays a recorded
     * stream for the time specified in the Duration field. (If a stream ends before the time specified in the Duration
     * field, playback ends when the stream ends.) If you pass a negative number other  than -1 in the Duration field,
     * it interprets the value as if it were -1.
     */
    private Double duration;

    /**
     * An optional Boolean value or number that specifies whether to flush any previous playlist.
     */
    private Boolean reset;

    @Override
    public String toReadableString() {
        return null;
    }

    @Override
    public byte[] composeOutMessageToBytes() {
        throw new OperationNotSupportException("not support");
    }

    @Override
    public void continueDecode(ByteBuf in) {
        commandObject = AMF0.decodeAMF0Type(in);
        streamName = (String) AMF0.decodeAMF0Type(in);
        start = (Double) AMF0.decodeAMF0Type(in);
        duration = (Double) AMF0.decodeAMF0Type(in);
        reset = (Boolean) AMF0.decodeAMF0Type(in);
    }
}

