package fi.methics.musap.sdk.internal.util;

import static org.junit.Assert.*;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class StringUtilTest {

    @Test
    public void toUTF8String() {
        String s = "teststring";
        assertEquals(s, StringUtil.toUTF8String(s.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void toUTF8Bytes() {
        String s = "teststring";
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(b, StringUtil.toUTF8Bytes(s));
    }
}