package cn.drelang.live.server.rtmp.entity;

/**
 * TODO:
 *
 * @author Drelang
 * @date 2021/3/6 15:05
 */

public class Constants {

    private Constants(){}

    /** message type id */
    public static final byte SET_CHUNK_SIZE = 0x01;
    public static final byte ABORT_MESSAGE = 0x02;
    public static final byte ACKNOWLEDGEMENT = 0x03;
    public static final byte USER_CONTROL_MESSAGE = 0x04;
    public static final byte ACKNOWLEDGEMENT_WINDOW_SIZE = 0x05;
    public static final byte SET_PEER_BANDWIDTH = 0x06;
    public static final byte AUDIO_MESSAGE = 0x08;
    public static final byte VIDEO_MESSAGE = 0x09;
    public static final byte METADATA_AMF3 = 0x0F;
    public static final byte SHARED_OBJECT_AMF3 = 0x10;
    public static final byte COMMAND_MESSAGE_AMF3 = 0x11;
    public static final byte METADATA_AMF0 = 0x12;
    public static final byte SHARED_OBJECT_AMF0 = 0x13;
    public static final byte COMMAND_MESSAGE_AMF0 = 0x14;
    public static final byte AGGREGATE_MESSAGE = 0x16;

    /** AMF0 marker */
    public static final byte AMF0_NUMBER = 0x00;
    public static final byte AMF0_BOOLEAN = 0x01;
    public static final byte AMF0_STRING = 0x02;
    public static final byte AMF0_OBJECT = 0x03;
    public static final byte AMF0_MOVIE_CLIP = 0x04; // reserved, not supported
    public static final byte AMF0_NULL = 0x05;
    public static final byte AMF0_UNDEFINED = 0x06;
    public static final byte AMF0_REFERENCE = 0x07;
    public static final byte AMF0_ECMA_ARRAY = 0x08;
    public static final byte AMF0_OBJECT_END = 0x09;
    public static final byte AMF0_STRICT_ARRAY = 0x0A;
    public static final byte AMF0_DATE = 0x0B;
    public static final byte AMF0_LONG_STRING = 0x0C;
    public static final byte AMF0_UNSUPPORTED = 0x0D;
    public static final byte AMF0_RECORDSET = 0x0E;  // reserved, not supported
    public static final byte AMF0_XML_DOCUMENT = 0x0F;
    public static final byte AMF0_TYPED_OBJECT = 0x10;
    public static final byte AMF0_AVMPLUS_OBJECT = 0x11; // upgrade to AMF3

    /** RTMP */
    public static final byte PROTOCOL_CONTROL_MESSAGE_CSID = 0x02;  // channel stream id
    public static final byte PROTOCOL_CONTROL_MESSAGE_MSID = 0x00;  // message stream id
}

