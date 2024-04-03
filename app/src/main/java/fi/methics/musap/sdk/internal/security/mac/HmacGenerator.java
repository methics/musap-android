package fi.methics.musap.sdk.internal.security.mac;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import fi.methics.musap.sdk.internal.security.keygenerator.MusapKeyGenerator;
import fi.methics.musap.sdk.internal.security.keystorage.KeyStorage;
import fi.methics.musap.sdk.internal.util.HexUtil;
import fi.methics.musap.sdk.internal.util.MLog;


/**
 * Creates a HMAC for the given message. MUSAP and MUSAP Link use HMAC to authenticate messages.
 * The HMAC output is in hexadecimal.
 */
public class HmacGenerator implements MacGenerator {

    private final KeyStorage keyStorage;

    private static final String TAG = HmacGenerator.class.getSimpleName();

    private static final String HASH_ALGORITHM = "HmacSHA256";

    public HmacGenerator(KeyStorage keyStorage) {
        this.keyStorage = keyStorage;
    }

    @Override
    public String generate(String message, String iv, String transId, String type)
            throws GeneralSecurityException, IOException {
        return HexUtil.hexLine(this.generateHmacBytes(message, iv, transId, type));
    }

    @Override
    public boolean validate(String message, String iv, String transId, String type, String mac)
            throws GeneralSecurityException, IOException {

//        if (transId == null) {
//            throw new IllegalArgumentException("No TransID for MAC calculation");
//        }

        if (message == null) {
            throw new IllegalArgumentException("No message for MAC calculation");
        }

        if (iv == null) {
            throw new IllegalArgumentException("No IV for MAC calculation");
        }

        if (type == null) {
            throw new IllegalArgumentException("No type for MAC calculation");
        }

        if (mac == null) {
            throw new IllegalArgumentException("Missing MAC to validate");
        }

        byte[] calculatedHmac = this.generateHmacBytes(message, iv, transId, type);
        byte[] receivedHmac = HexUtil.parseHex(mac);

        MLog.d(TAG, "calc:" +HexUtil.hexLine(calculatedHmac));
        MLog.d(TAG, "recd:" +HexUtil.hexLine(receivedHmac));

        // This comparison is protected against side-channel attacks
        return MessageDigest.isEqual(calculatedHmac, receivedHmac);
    }

    private static byte[] hmac(SecretKey key, byte[] message)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HmacGenerator.HASH_ALGORITHM);
        mac.init(key);
        return mac.doFinal(message);
    }

    private byte[] generateHmacBytes(String message, String iv, String transId, String type)
            throws GeneralSecurityException, IOException {

        // Some message types do not use trans id. In these cases use empty string
        if (transId == null) {
            transId = "";
        }

        MLog.d(TAG, "Message=" + message +  ", iv=" + iv + ", transId=" + transId + ", type=" + type);

        String input = transId + type + iv + message;
        MLog.d(TAG, "Input=" + input);

        SecretKey macKey  = this.keyStorage.loadKey(MusapKeyGenerator.MAC_KEY_ALIAS);

        if (macKey == null) {
            throw new IllegalArgumentException("Missing MAC key. You must generate a key first");
        }

        MLog.d(TAG, "Alg=" + macKey.getAlgorithm() + ", Format=" + macKey.getFormat());
        return hmac(macKey, input.getBytes(StandardCharsets.UTF_8));
    }
}
