package fi.methics.musap.sdk.internal.datatype;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;

import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.coupling.payload.EnrollDataPayload;
import fi.methics.musap.sdk.internal.datatype.coupling.EnrollDataResponsePayload;
import fi.methics.musap.sdk.internal.datatype.coupling.payload.ExternalSignaturePayload;
import fi.methics.musap.sdk.internal.datatype.coupling.ExternalSignatureResponsePayload;
import fi.methics.musap.sdk.internal.datatype.coupling.payload.LinkAccountPayload;
import fi.methics.musap.sdk.internal.datatype.coupling.LinkAccountResponsePayload;
import fi.methics.musap.sdk.internal.datatype.coupling.PollResponsePayload;
import fi.methics.musap.sdk.internal.datatype.coupling.SignatureCallbackPayload;
import fi.methics.musap.sdk.internal.datatype.coupling.SignaturePayload;
import fi.methics.musap.sdk.internal.datatype.coupling.payload.PollPayload;
import fi.methics.musap.sdk.internal.datatype.coupling.payload.UpdateDataPayload;
import fi.methics.musap.sdk.internal.datatype.coupling.UpdateDataResponsePayload;
import fi.methics.musap.sdk.internal.security.encryption.AesTransportEncryption;
import fi.methics.musap.sdk.internal.security.encryption.PayloadHolder;
import fi.methics.musap.sdk.internal.security.encryption.TransportEncryption;
import fi.methics.musap.sdk.internal.security.keygenerator.MusapKeyGenerator;
import fi.methics.musap.sdk.internal.security.keystorage.AndroidKeyStorage;
import fi.methics.musap.sdk.internal.security.keystorage.KeyStorageFactory;
import fi.methics.musap.sdk.internal.security.mac.HmacGenerator;
import fi.methics.musap.sdk.internal.security.mac.MacGenerator;
import fi.methics.musap.sdk.internal.util.ByteaMarshaller;
import fi.methics.musap.sdk.internal.util.MLog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MusapLink {

    public static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private static final String ENROLL_MSG_TYPE = "enrolldata";

    private static final String KEY_CALLBACK_MSG_TYPE = "generatekeycallback";

    /**
     * How many times to poll for a signature response.
     * For now, we use a slightly excessive number to prevent unwanted errors in testing.
     */
    private static final int POLL_AMOUNT = 200;

    /**
     * How often app polls for a signature.
     * For now, we use a slightly excessive number to prevent unwanted errors in testing.
     */
    private static final int POLL_INTERVAL_MS = 2000;

    // Okhttp connect timeout milliseconds
    private static final long connectTimeOutMs = 180*1000;
    private static final long callTimeOutMs = 240*1000;
    private static final long writeTimeoutMs = 180*1000;

    // Okhttp connect timeout milliseconds
    private static final long readTimeOutMs = 180*1000;

    // Okhttp connect timeout milliseconds
    private static final long shortConnectTimeOutMs = 10*1000;
    private static final long shortCallTimeOutMs = 20*1000;
    private static final long shortWriteTimeoutMs = 10*1000;

    // Okhttp connect timeout milliseconds
    private static final long shortReadTimeOutMs = 10*1000;

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(byte[].class, new ByteaMarshaller())
            .create();

    private String url;
    private String musapid;

    private static TransportEncryption encryption = new AesTransportEncryption(KeyStorageFactory.getAndroidKeyStorage());
    private static MacGenerator mac = new HmacGenerator(KeyStorageFactory.getAndroidKeyStorage());

    public MusapLink(String url, String musapid) {
        this.url     = url;
        this.musapid = musapid;
    }

    /**
     * Set MUSAP ID
     * @param musapid MUSAP ID
     */
    public void setMusapId(String musapid) {
        this.musapid = musapid;
    }

    /**
     * Get MUSAP ID
     * @return MUSAP ID
     */
    public String getMusapId() {
        return this.musapid;
    }

    /**
     * Enroll this Musap instance with a MUSAP link.
     * @param fcmToken
     * @return MusapLink with updated data.
     * @throws IOException
     */
    public MusapLink enroll(String fcmToken)
            throws IOException, GeneralSecurityException, MusapException {
        String secret = MusapKeyGenerator.hkdfStatic();
        EnrollDataPayload payload = new EnrollDataPayload(fcmToken, secret);

        MusapMessage msg = new MusapMessage();
        msg.setPayload(payload);
        msg.setType(payload);

        MusapMessage respMsg = this.sendRequest(msg);
        MLog.d("Response payload=" + respMsg.payload);

        EnrollDataResponsePayload respPayload = GSON.fromJson(respMsg.payload, EnrollDataResponsePayload.class);

        this.musapid = respPayload.getMusapId();
        return this;
    }

    /**
     * Update FCM token in MUSAP Link.
     * @param fcmToken
     * @return true if update was accepted by MUSAP Link
     * @throws IOException
     */
    public boolean updateFcmToken(String fcmToken)
            throws IOException, GeneralSecurityException, MusapException {
        UpdateDataPayload reqPayload = new UpdateDataPayload(fcmToken);

        MusapMessage msg = new MusapMessage();
        msg.setPayload(reqPayload);
        msg.setType(reqPayload);

        MusapMessage respMsg = this.sendRequest(msg);

        UpdateDataResponsePayload respPayload = GSON.fromJson(respMsg.payload, UpdateDataResponsePayload.class);
        return respPayload.isSuccess();
    }

    /**
     * Couple this MUSAP with a MUSAP Link.
     * This performs networking operations.
     * @return RelyingParty if pairing was a success.
     * @throws IOException
     */
    public RelyingParty couple(String couplingCode, String uuid)
            throws IOException, MusapException, GeneralSecurityException {
        LinkAccountPayload payload = new LinkAccountPayload(couplingCode, uuid);

        MusapMessage msg = new MusapMessage();
        msg.setPayload(payload);
        msg.setType(payload);

        msg.musapId = this.musapid;

        MusapMessage respMsg = this.sendRequest(msg);

        if (respMsg == null || respMsg.payload == null) {
            MLog.d("Null payload");
            return null;
        }

        LinkAccountResponsePayload resp = GSON.fromJson(respMsg.payload, LinkAccountResponsePayload.class);
        MLog.d("Parsed payload");
        if (resp.isSuccess()) {
            return new RelyingParty(resp);
        } else {
            return null;
        }

    }

    /**
     * Poll for a signature request
     * This performs networking operations.
     * @return SignaturePayload if poll returned data. Otherwise null.
     * @throws IOException
     */
    public PollResponsePayload poll()
            throws IOException, MusapException, GeneralSecurityException {
        MusapMessage msg = new MusapMessage();
        PollPayload reqPayload = new PollPayload();

        msg.musapId = this.musapid;
        msg.setPayload(reqPayload);
        msg.setType(reqPayload);

        MusapMessage respMsg = this.sendRequest(msg, this.buildShortTimeoutClient());

        if (respMsg == null || respMsg.payload == null) {
            MLog.d("Null payload");
            return null;
        }

        String transId = respMsg.transid;

        SignaturePayload payload = GSON.fromJson(respMsg.payload, SignaturePayload.class);
        MLog.d("Parsed payload");

        return new PollResponsePayload(payload, transId);
    }

    /**
     * Send a key generation callback to MUSAP Link.
     * This performs networking operations.
     *
     * @param key MusapKey
     * @param transId transaction id
     * @see #poll()
     * @throws IOException
     */
    public void sendKeygenCallback(MusapKey key, String transId)
            throws IOException, GeneralSecurityException, MusapException {

        // keygen and signature callbacks are similar atm
        SignatureCallbackPayload payload = new SignatureCallbackPayload(key);

        MusapMessage msg = new MusapMessage();
        msg.musapId = this.musapid;
        msg.transid = transId;
        msg.setPayload(payload);
        // Override type on purpose. Signature callback has a different type
        msg.type = KEY_CALLBACK_MSG_TYPE;

        // TODO: Check response.
        this.sendRequest(msg, this.buildClient());
    }

    /**
     * Send a signature callback to MUSAP Link.
     * This performs networking operations.
     *
     * @param signature MusapSignature
     * @param transId transaction id
     * @see #poll()
     * @throws IOException
     */
    public void sendSignatureCallback(MusapSignature signature, String transId)
            throws IOException, MusapException, GeneralSecurityException {

        SignatureCallbackPayload payload = new SignatureCallbackPayload(signature);
        payload.attestationResult = signature.getKeyAttestationResult();

        MusapMessage msg = new MusapMessage();
        msg.setType(payload);
        msg.setPayload(payload);
        msg.musapId = this.musapid;
        msg.transid = transId;

        MusapMessage respMsg = this.sendRequest(msg);

        if (respMsg == null || respMsg.payload == null) {
            MLog.d("Null payload");
        }
    }

    /**
     * Request external Signature with the "externalsignature" Coupling API call
     * @param payload External Signature request payload
     * @return External Signature response payload
     * @throws IOException if request could not be sent
     * @throws MusapException if signature failed
     */
    public ExternalSignatureResponsePayload sign(ExternalSignaturePayload payload)
            throws IOException, MusapException, GeneralSecurityException {
        MLog.d("MUSAP Link sign");

        MusapMessage msg = new MusapMessage();
        msg.setPayload(payload);
        msg.setType(payload);
        msg.musapId = getMusapId();

        MusapMessage respMsg = sendRequest(msg);
        if (respMsg == null || respMsg.payload == null) {
            MLog.d("Null payload");
            return null;
        }

        ExternalSignatureResponsePayload resp = GSON.fromJson(respMsg.payload, ExternalSignatureResponsePayload.class);
        if ("pending".equals(resp.status)) {
            return pollForSignature(resp.transid);
        }
        if ("failed".equals(resp.status)) {
            throw new MusapException(resp.getErrorCode(), "Signature failed");
        }
        return resp;
    }

    private MusapMessage sendRequest(MusapMessage msg)
            throws IOException, MusapException, GeneralSecurityException {
        return sendRequest(msg, this.buildClient());
    }

    /**
     * Send a MUSAP Coupling API message
     *
     * @param msg Request
     * @return Response or null if not available
     * @throws IOException
     */
    private MusapMessage sendRequest(MusapMessage msg, OkHttpClient client)
            throws IOException, GeneralSecurityException, MusapException {
        MLog.d("Sending request " + msg.toJson());
        MLog.d("Target URL " + this.url);

        boolean shouldEncrypt = this.shouldEncrypt();

        // If the request should be encrypted, rewrite payload with the encrypted variant
        // Enroll request is not encrypted, but response is.
        if (shouldEncrypt && !msg.type.equalsIgnoreCase(ENROLL_MSG_TYPE)) {
            PayloadHolder holder = this.getPayload(msg.payload, shouldEncrypt);
            msg.payload = holder.getPayload();
            msg.iv = holder.getIv();
            msg.mac = mac.generate(msg.payload, msg.iv, msg.getIdentifier(), msg.type);
        }

        RequestBody body = RequestBody.create(msg.toJson(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(this.url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String sResp = response.body().string();
                MLog.d("Got response " + sResp);

                MusapMessage respMsg = GSON.fromJson(sResp, MusapMessage.class);
                if (respMsg == null || respMsg.payload == null) {
                    MLog.d("Null payload");
                    return null;
                }

                // TODO: For now, only warn about mac violations
                try {
                    if (shouldEncrypt && !isMacValid(respMsg)) {
                        MLog.e("Invalid mac");
//                        throw new MusapException("Invalid message");
                    }
                } catch (Exception e) {
                    MLog.e("Invalid mac", e);
                }
                respMsg.payload = this.parsePayload(respMsg, this.shouldEncrypt());

                return respMsg;
            } else {
                MLog.d("Null response");
                return null;
            }
        }
    }

    private boolean isMacValid(MusapMessage msg) throws GeneralSecurityException, IOException {
        MLog.d("Validating MAC");
        return mac.validate(msg.payload, msg.iv, msg.getIdentifier(), msg.type, msg.mac);
    }

    /**
     * Poll for a signature response
     * @param transid       Transaction ID
     * @return Signature response payload
     * @throws IOException if request could not be sent
     * @throws MusapException if signature failed
     */
    private ExternalSignatureResponsePayload pollForSignature(String transid) throws IOException, MusapException, GeneralSecurityException {
        MLog.d("Polling for signature");
        // Build a client for polling
        OkHttpClient client = this.buildClient();

        for (int i = 0; i < POLL_AMOUNT; i++) {
            MLog.d("Poll attempt " + i);
            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                MLog.d("Poll interrupted");
                continue;
            }

            ExternalSignaturePayload payload = new ExternalSignaturePayload();
            payload.transid = transid;

            MusapMessage msg = new MusapMessage();
            msg.setPayload(payload);
            msg.setType(payload);
            msg.musapId = getMusapId();

            MusapMessage respMsg = sendRequest(msg, client);
            if (respMsg == null || respMsg.payload == null) {
                MLog.d("Null payload");
                return null;
            }

            ExternalSignatureResponsePayload resp = GSON.fromJson(respMsg.payload, ExternalSignatureResponsePayload.class);
            if ("pending".equals(resp.status)) {
                continue;
            }
            if ("failed".equals(resp.status)) {
                throw new MusapException(resp.getErrorCode(), "Signature failed");
            }
            return resp;
        }
        return null;
    }

    private PayloadHolder getPayload(String payloadBase64, boolean encrypt)
            throws GeneralSecurityException, IOException {
        if (encrypt) {
            // Return encrypted payload and IV
            return encryption.encrypt(payloadBase64);
        } else {
            // Just return the payload and null IV
            return new PayloadHolder(payloadBase64, null);
        }
    }

    public String parsePayload(MusapMessage respMsg, boolean isEncrypted)
            throws GeneralSecurityException, IOException {
        MLog.d("Response payload=" + respMsg.payload);
        if (isEncrypted) {
            byte[] decoded = Base64.decode(respMsg.payload.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
            return encryption.decrypt(decoded, respMsg.iv);
        } else {
            String payloadJson = new String(Base64.decode(respMsg.payload, Base64.NO_WRAP));
            MLog.d("Decoded=" + payloadJson);
            return payloadJson;
        }
    }

    /**
     * Check if we should encrypt messages to Musap link.
     * To allow backwards compatibility, old accounts that do not have
     * generated keys do not try to encrypt. New accounts will use it.
     * @return
     */
    private boolean shouldEncrypt() {
        try {
            // If we have generated a key, use it. Authn and sign key
            // are generated at the same time, so if one exists, the another exists too.
            return new AndroidKeyStorage().keyExists(MusapKeyGenerator.MAC_KEY_ALIAS);
        } catch (Exception e) {
            MLog.e("Failed to find key",e);
            // If there is a problem with key existance, don't encrypt
            return false;
        }
    }

    private OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .readTimeout(readTimeOutMs, TimeUnit.MILLISECONDS)
                .connectTimeout(connectTimeOutMs, TimeUnit.MILLISECONDS)
                .callTimeout(callTimeOutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeoutMs, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * Build an Okhttp client with shorter timeouts.
     * Good for polling and other noncritical requests.
     * @return
     */
    private OkHttpClient buildShortTimeoutClient() {
        return new OkHttpClient.Builder()
                .readTimeout(shortReadTimeOutMs, TimeUnit.MILLISECONDS)
                .connectTimeout(shortConnectTimeOutMs, TimeUnit.MILLISECONDS)
                .callTimeout(shortCallTimeOutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(shortWriteTimeoutMs, TimeUnit.MILLISECONDS)
                .build();
    }
}
