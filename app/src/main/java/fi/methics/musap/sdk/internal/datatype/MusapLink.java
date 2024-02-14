package fi.methics.musap.sdk.internal.datatype;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.coupling.LinkAccountPayload;
import fi.methics.musap.sdk.internal.datatype.coupling.LinkAccountResponsePayload;
import fi.methics.musap.sdk.internal.datatype.coupling.EnrollDataPayload;
import fi.methics.musap.sdk.internal.datatype.coupling.EnrollDataResponsePayload;
import fi.methics.musap.sdk.internal.datatype.coupling.ExternalSignaturePayload;
import fi.methics.musap.sdk.internal.datatype.coupling.ExternalSignatureResponsePayload;
import fi.methics.musap.sdk.internal.datatype.coupling.PollResponsePayload;
import fi.methics.musap.sdk.internal.datatype.coupling.SignatureCallbackPayload;
import fi.methics.musap.sdk.internal.datatype.coupling.SignaturePayload;
import fi.methics.musap.sdk.internal.datatype.coupling.UpdateDataPayload;
import fi.methics.musap.sdk.internal.datatype.coupling.UpdateDataResponsePayload;
import fi.methics.musap.sdk.internal.util.ByteaMarshaller;
import fi.methics.musap.sdk.internal.util.MLog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MusapLink {

    public static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private static final String COUPLE_MSG_TYPE       = "linkaccount";
    private static final String ENROLL_MSG_TYPE       = "enrolldata";
    private static final String UPDATE_MSG_TYPE       = "updatedata";
    private static final String POLL_MSG_TYPE         = "getdata";
    private static final String SIG_CALLBACK_MSG_TYPE = "signaturecallback";
    private static final String KEY_CALLBACK_MSG_TYPE = "generatekeycallback";
    private static final String SIGN_MSG_TYPE         = "externalsignature";

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


    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(byte[].class, new ByteaMarshaller()).create();

    private String url;
    private String musapid;

    private String aesKey;
    private String macKey;

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

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public void setMacKey(String macKey) {
        this.macKey = macKey;
    }

    public void encrypt(MusapMessage msg) {
        // TODO
    }

    public void decrypt(MusapMessage msg) {
        // TODO
    }

    /**
     * Enroll this Musap instance with a MUSAP link.
     * @param fcmToken
     * @return MusapLink with updated data.
     * @throws IOException
     */
    public MusapLink enroll(String fcmToken) throws IOException {
        EnrollDataPayload payload = new EnrollDataPayload(fcmToken);

        MusapMessage msg = new MusapMessage();
        msg.payload = payload.toBase64();
        msg.type = ENROLL_MSG_TYPE;
        MLog.d("Message=" + msg.toJson());
        MLog.d("Url=" + this.url);

        RequestBody body = RequestBody.create(msg.toJson(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(this.url)
                .post(body)
                .build();
        OkHttpClient client = this.buildClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String sResp = response.body().string();
                MLog.d("Got response " + sResp);

                MusapMessage respMsg = GSON.fromJson(sResp, MusapMessage.class);
                MLog.d("Response payload=" + respMsg.payload);
                String payloadJson = new String(Base64.decode(respMsg.payload, Base64.NO_WRAP));
                MLog.d("Decoded=" + payloadJson);
                EnrollDataResponsePayload respPayload = GSON.fromJson(payloadJson, EnrollDataResponsePayload.class);

                this.musapid = respPayload.getMusapId();
                return this;
            } else {
                MLog.d("Null response");
                throw new IOException("EnrollData failed. Got empty response.");
            }
        }
    }

    /**
     * Update FCM token in MUSAP Link.
     * @param fcmToken
     * @return true if update was accepted by MUSAP Link
     * @throws IOException
     */
    public boolean updateFcmToken(String fcmToken) throws IOException {
        UpdateDataPayload payload = new UpdateDataPayload(fcmToken);

        MusapMessage msg = new MusapMessage();
        msg.payload = payload.toBase64();
        msg.type = UPDATE_MSG_TYPE;
        MLog.d("Message=" + msg.toJson());
        MLog.d("Url=" + this.url);

        RequestBody body = RequestBody.create(msg.toJson(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(this.url)
                .post(body)
                .build();
        OkHttpClient client = this.buildClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String sResp = response.body().string();
                MLog.d("Got response " + sResp);

                MusapMessage respMsg = GSON.fromJson(sResp, MusapMessage.class);
                MLog.d("Response payload=" + respMsg.payload);
                String payloadJson = new String(Base64.decode(respMsg.payload, Base64.NO_WRAP));
                MLog.d("Decoded=" + payloadJson);
                UpdateDataResponsePayload respPayload = GSON.fromJson(payloadJson, UpdateDataResponsePayload.class);
                return respPayload.isSuccess();
            } else {
                MLog.d("Null response");
                throw new IOException("EnrollData failed. Got empty response.");
            }
        }
    }

    /**
     * Couple this MUSAP with a MUSAP Link.
     * This performs networking operations.
     * @return RelyingParty if pairing was a success.
     * @throws IOException
     */
    public RelyingParty couple(String couplingCode, String uuid) throws IOException {
        LinkAccountPayload payload = new LinkAccountPayload(couplingCode, uuid);

        MusapMessage msg = new MusapMessage();
        msg.payload = payload.toBase64();
        msg.type = COUPLE_MSG_TYPE;
        MLog.d("Message=" + msg.toJson());
        MLog.d("Url=" + this.url);

        RequestBody body = RequestBody.create(msg.toJson(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(this.url)
                .post(body)
                .build();
        OkHttpClient client = this.buildClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String sResp = response.body().string();
                MLog.d("Got response " + sResp);

                MusapMessage respMsg = GSON.fromJson(sResp, MusapMessage.class);

                if (respMsg.payload == null) {
                    MLog.d("Null payload");
                    return null;
                }

                MLog.d("Response payload=" + respMsg.payload);
                String payloadJson = new String(Base64.decode(respMsg.payload, Base64.NO_WRAP));
                MLog.d("Decoded=" + payloadJson);

                LinkAccountResponsePayload resp = GSON.fromJson(payloadJson, LinkAccountResponsePayload.class);
                MLog.d("Parsed payload");
                if (resp.isSuccess()) {
                    return new RelyingParty(resp);
                } else {
                    return null;
                }
            } else {
                MLog.d("Null response");
                return null;
            }
        }
    }


    /**
     * Poll for a signature request
     * This performs networking operations.
     * @return SignaturePayload if poll returned data. Otherwise null.
     * @throws IOException
     */
    public PollResponsePayload poll() throws IOException {
        MusapMessage msg = new MusapMessage();
        msg.type = POLL_MSG_TYPE;
        msg.musapId = this.musapid;
        MLog.d("Message=" + msg.toJson());
        MLog.d("Url=" + this.url);

        RequestBody body = RequestBody.create(msg.toJson(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(this.url)
                .post(body)
                .build();
        OkHttpClient client = this.buildClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String sResp = response.body().string();
                MLog.d("Got response " + sResp);
                MusapMessage respMsg = GSON.fromJson(sResp, MusapMessage.class);

                if (respMsg == null || respMsg.payload == null) {
                    MLog.d("Null payload");
                    return null;
                }

                String transId = respMsg.transid;
                MLog.d("Response payload=" + respMsg.payload);
                String payloadJson = new String(Base64.decode(respMsg.payload, Base64.NO_WRAP));
                MLog.d("Decoded=" + payloadJson);

                SignaturePayload payload = GSON.fromJson(payloadJson, SignaturePayload.class);
                MLog.d("Parsed payload");

                return new PollResponsePayload(payload, transId);
            } else {
                MLog.d("Null response");
                return null;
            }
        }
    }

    /**
     * Send a key generation callback to MUSAP Link.
     * This performs networking operations.
     *
     * @param key MusapKey
     * @param transId transaction id
     * @throws IOException
     */
    public void sendKeygenCallback(MusapKey key, String transId) throws IOException {
        SignatureCallbackPayload payload = new SignatureCallbackPayload(key);

        MusapMessage msg = new MusapMessage();
        msg.type = KEY_CALLBACK_MSG_TYPE;
        msg.payload = payload.toBase64();
        msg.musapId = this.musapid;
        msg.transid = transId;
        MLog.d("Message=" + msg.toJson());
        MLog.d("Url=" + this.url);

        RequestBody body = RequestBody.create(msg.toJson(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(this.url)
                .post(body)
                .build();
        OkHttpClient client = this.buildClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String sResp = response.body().string();
                MLog.d("Got response " + sResp);
                MusapMessage respMsg = GSON.fromJson(sResp, MusapMessage.class);

                if (respMsg == null || respMsg.payload == null) {
                    MLog.d("Null payload");
                }
            }
        }
    }

    /**
     * Send a signature callback to MUSAP Link.
     * This performs networking operations.
     *
     * @param signature MusapSignature
     * @param transId transaction id
     * @throws IOException
     */
    public void sendSignatureCallback(MusapSignature signature, String transId) throws IOException {

        SignatureCallbackPayload payload = new SignatureCallbackPayload(signature);

        MusapMessage msg = new MusapMessage();
        msg.type = SIG_CALLBACK_MSG_TYPE;
        msg.payload = payload.toBase64();
        msg.musapId = this.musapid;
        msg.transid = transId;
        MLog.d("Message=" + msg.toJson());
        MLog.d("Url=" + this.url);

        RequestBody body = RequestBody.create(msg.toJson(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(this.url)
                .post(body)
                .build();
        OkHttpClient client = this.buildClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String sResp = response.body().string();
                MLog.d("Got response " + sResp);
                MusapMessage respMsg = GSON.fromJson(sResp, MusapMessage.class);

                if (respMsg == null || respMsg.payload == null) {
                    MLog.d("Null payload");
                }
            }
        }
    }
  
    /**
     * Request external Signature with the "externalsignature" Coupling API call
     * @param payload External Signature request payload
     * @return External Signature response payload
     * @throws IOException if request could not be sent
     * @throws MusapException if signature failed
     */
    public ExternalSignatureResponsePayload sign(ExternalSignaturePayload payload) throws IOException, MusapException {
        MLog.d("MUSAP Link sign");

        MusapMessage msg = new MusapMessage();
        msg.payload = payload.toBase64();
        msg.type    = SIGN_MSG_TYPE;
        msg.musapId = getMusapId();
        MLog.d("Message=" + msg.toJson());

        MusapMessage respMsg = sendRequest(msg);
        if (respMsg == null || respMsg.payload == null) {
            MLog.d("Null payload");
            return null;
        }
        MLog.d("Response payload=" + respMsg.payload);
        String payloadJson = new String(Base64.decode(respMsg.payload, Base64.NO_WRAP));
        MLog.d("Decoded=" + payloadJson);

        ExternalSignatureResponsePayload resp = GSON.fromJson(payloadJson, ExternalSignatureResponsePayload.class);
        if ("pending".equals(resp.status)) {
            return pollForSignature(resp.transid);
        }
        if ("failed".equals(resp.status)) {
            throw new MusapException(resp.getErrorCode(), "Signature failed");
        }
        return resp;
    }

    private MusapMessage sendRequest(MusapMessage msg) throws IOException {
        return sendRequest(msg, this.buildClient());
    }

        /**
         * Send a MUSAP Coupling API message
         * @param msg Request
         * @return Response or null if not available
         * @throws IOException
         */
    private MusapMessage sendRequest(MusapMessage msg, OkHttpClient client) throws IOException {
        MLog.d("Sending request " + msg.toJson());
        MLog.d("Target URL " + this.url);

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
                return respMsg;
            } else {
                MLog.d("Null response");
                return null;
            }
        }
    }

    /**
     * Poll for a signature response
     * @param transid       Transaction ID
     * @return Signature response payload
     * @throws IOException if request could not be sent
     * @throws MusapException if signature failed
     */
    private ExternalSignatureResponsePayload pollForSignature(String transid) throws IOException, MusapException {
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
            msg.payload = payload.toBase64();
            msg.type    = SIGN_MSG_TYPE;
            msg.musapId = getMusapId();
            MLog.d("Message=" + msg.toJson());

            MusapMessage respMsg = sendRequest(msg, client);
            if (respMsg == null || respMsg.payload == null) {
                MLog.d("Null payload");
                return null;
            }
            MLog.d("Response payload=" + respMsg.payload);
            String payloadJson = new String(Base64.decode(respMsg.payload, Base64.NO_WRAP));
            MLog.d("Decoded=" + payloadJson);
            ExternalSignatureResponsePayload resp = GSON.fromJson(payloadJson, ExternalSignatureResponsePayload.class);
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

    private OkHttpClient buildClient() {
        // TODO: App can just use one client.
        //  Maybe have a short timeout and long timeout clients?
        return new OkHttpClient.Builder()
                .readTimeout(readTimeOutMs, TimeUnit.MILLISECONDS)
                .connectTimeout(connectTimeOutMs, TimeUnit.MILLISECONDS)
                .callTimeout(callTimeOutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeoutMs, TimeUnit.MILLISECONDS)
                .build();
    }

}
