package fi.methics.musap.sdk.internal.util;

import static org.junit.Assert.*;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

public class HexUtilTest {

    @Test
    public void hexLine() {
        String expected = "000102030405060708090a0b0c0d0e0f";
        byte[] b = new byte[16];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) i;
        }

        String line = HexUtil.hexLine(b);
        assertEquals(expected, line);
    }

    @Test
    public void testHexLine() {
        String expected = "000102030405060708090A0B0C0D0E0F";
        byte[] b = new byte[16];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) i;
        }

        String line = HexUtil.hexLine(b, false, true);
        assertEquals(expected, line);
    }

    @Test
    public void testHexLine1() {
        String expected = "00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F";
        byte[] b = new byte[16];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) i;
        }

        String line = HexUtil.hexLine(b, true, true);
        assertEquals(expected, line);
    }

    @Test
    public void parseHex() {
        String expected = "000102030405060708090a0b0c0d0e0f";
        byte[] b = new byte[16];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) i;
        }

        assertArrayEquals(b, HexUtil.parseHex(expected));
    }
}