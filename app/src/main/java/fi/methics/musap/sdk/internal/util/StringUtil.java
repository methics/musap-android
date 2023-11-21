package fi.methics.musap.sdk.internal.util;

import java.nio.charset.StandardCharsets;

public class StringUtil {

    /**
     * Convert the byte[] to an UTF-8 String
     * @param bytes byte[]
     * @return UTF-8 String
     */
    public static String toUTF8String(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Convert the String to UTF-8 bytes
     * @param string String
     * @return byte[]
     */
    public static byte[] toUTF8Bytes(String string) {
        if (string == null) {
            return null;
        }
        return string.getBytes(StandardCharsets.UTF_8);
    }

}
