//
//  (c) Copyright 2003-2019 Methics Technologies Oy. All rights reserved. 
//

package fi.methics.musap.sdk.sscd.rest204;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import fi.methics.musap.sdk.internal.util.MLog;
import okhttp3.OkHttpClient;

/**
 * Simple HTTP Client used for HMAC authentication
 */
public class RestHmac {

    private OkHttpClient client;
    private String userId;
    private String apiKey;
    
    private static final String RFC2822_PATTERN = "EEE, dd MMM yyyy HH:mm:ss Z";
    private static final SimpleDateFormat RFC2822 = new SimpleDateFormat(RFC2822_PATTERN, Locale.US);

    public RestHmac(OkHttpClient client, String userId, String apiKey) {
        this.client = client;
        this.userId = userId;
        this.apiKey = apiKey;
    }

    /**
     * Convert byte[] to hex
     * @param bytes byte[] to convert
     * @return Hex String
     */
    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Calculate a HMAC digest
     * @param content Content to digest
     * @param skey    HMAC secret key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static byte[] digest(final String content, final String skey) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] contentBytes = content.getBytes();
        Mac mac = Mac.getInstance("HMACSHA256");
        SecretKeySpec macKey = new SecretKeySpec(skey.getBytes(), "RAW");
        mac.init(macKey);
        return mac.doFinal(contentBytes);
    }

    /**
     * Return HTTP Basic Authentication ("Authorization" and "Date") headers.
     *
     * @param method request HTTP method
     * @param url request URL
     * @param params request body parameters stringified
     */
    public Map<String, String> requestHeaders(final String method,
                                              final URL url,
                                              String params) throws IOException {
        params = (params == null) ? "" : params;
        String date = RFC2822.format(new Date());

        String[] values = new String[] { date, method, url.getHost(), url.getPath(), params };
        MLog.d("Checksum data: " + Arrays.toString(values));

        StringBuilder data = new StringBuilder();
        for (String val : values) {
            data.append(val);
            data.append("\n");
        }

        String sig;
        try {
            sig = bytesToHex(digest(data.toString(), apiKey));
        } catch (Exception e) {
            MLog.e("Crypto error while computing HMAC request signature", e);
            throw new IOException("Crypto error while computing HMAC request signature");
        }

        String auth = userId + ":" + sig;

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Date", date);
        headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()));
        headers.put("Content-Type",  "application/json");

        return headers;
    }

}
