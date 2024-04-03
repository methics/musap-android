package fi.methics.musap.sdk.internal.util;


import java.io.ByteArrayOutputStream;

/**
 * Produce and parse hexadecimal presentation of bytes.
 * Copied from MSSP HexUtil.
 */
public class HexUtil {

    /** Hex digits in lower case */
    private static final char[] hexDigits = {
            '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'
    };
    /** Hex digits in upper case */
    private static final char[] hexDigitsUp = {
            '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };


    /**
     * Constructs a hexadecimal string representation of the buffer.
     * NOTE: DON't change this method, because it's not just for logging.
     *
     * @param buf the buffer to dump.
     * @param spaces include spaces between numbers or not.
     * @param toupper use upper case letters for hex digits A-F.
     * @return the hexadecimal string.
     */
    public static String hexLine(final byte [] buf,
                                 final boolean spaces,
                                 final boolean toupper)
    {
        if (buf != null) {
            int l = buf.length*2; // length estimator
            if (spaces)
                l += buf.length;
            final StringBuilder sb = new StringBuilder(l);
            hexLine(sb, buf, spaces, toupper);
            return sb.toString();
        }
        return "";
    }

    /**
     * Constructs a hexadecimal string representation of the buffer.
     * NOTE: DON't change this method, because it's not just for logging.
     *
     * @param sb Output the text to this StringBuilder.
     * @param buf the buffer to dump.
     * @param spaces include spaces between numbers or not.
     * @param toupper use upper case letters for hex digits A-F.
     */
    public static void hexLine(final StringBuilder sb,
                               final byte[] buf,
                               final boolean spaces,
                               final boolean toupper)
    {
        for (int i = 0; buf != null && i < buf.length; ++i) {
            final byte b = buf[i];
            if (spaces && i > 0)
                sb.append(' ');
            if (toupper) {
                sb.append(hexDigitsUp[(b >> 4) & 0x0F]);
                sb.append(hexDigitsUp[b & 0x0F]);
            } else {
                sb.append(hexDigits[(b >> 4) & 0x0F]);
                sb.append(hexDigits[b & 0x0F]);
            }
        }
    }


    /**
     * Constructs a hexadecimal string representation of the buffer.
     * The hex digits A-F are in lower case, and there are no spaces in between bytes.
     * @see #hexLine(byte[],boolean,boolean)
     *
     * @param buf the buffer to dump.
     * @return the hexadecimal string.
     */
    public static String hexLine(final byte... buf) {
        return hexLine(buf, false, false);
    }

    /**
     * Parse HEX encoded string to bytes.
     * <p>
     * Input must consist of even number of hex digits without any whitespace/punctuation fillers.
     * Digits A-F can be upper or lower case.
     * <p>
     * @param hex input text
     * @return parse result
     * @throws IllegalArgumentException for bad length, or bad input digits.
     */
    public static byte[] parseHex(final CharSequence hex)
            throws IllegalArgumentException
    {
        if (hex == null) return null; // bad parse..

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final int l = hex.length();
        if ((l % 2) != 0) {
            throw new IllegalArgumentException("Input data length is no multiple of two chars");
        }
        for (int i = 0;i < l; i += 2) {
            char c1 = hex.charAt(i);
            if ('0' <= c1 && c1 <= '9') {
                c1 -= '0';
            } else if ('A' <= c1 && c1 <= 'F') {
                c1 -= ('A' - 10);
            } else if ('a' <= c1 && c1 <= 'f') {
                c1 -= ('a' - 10);
            } else {
                // bad input..
                throw new IllegalArgumentException("Invalid input char ("+c1+") on hex digit at offset "+i);
            }
            char c2 = hex.charAt(i+1);
            if ('0' <= c2 && c2 <= '9') {
                c2 -= '0';
            } else if ('A' <= c2 && c2 <= 'F') {
                c2 -= ('A' - 10);
            } else if ('a' <= c2 && c2 <= 'f') {
                c2 -= ('a' - 10);
            } else {
                // bad input..
                throw new IllegalArgumentException("Invalid input char ("+c2+") on hex digit at offset "+(i+1));
            }
            baos.write(((c1 & 0x0F) << 4) | (c2 & 0x0F));
        }
        return baos.toByteArray();
    }


}