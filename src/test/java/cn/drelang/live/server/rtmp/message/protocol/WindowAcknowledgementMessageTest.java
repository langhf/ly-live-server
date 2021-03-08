package cn.drelang.live.server.rtmp.message.protocol;

import org.junit.Test;

/**
 *
 * @author Drelang
 * @date 2021/3/8 22:29
 */
public class WindowAcknowledgementMessageTest {

    @Test
    public void testWAM() {
        WindowAcknowledgementMessage wam = new WindowAcknowledgementMessage();
        wam.setWindowSize(2500_000);
        System.out.println(wam);
        System.out.println(wam.toReadableString());
    }
}