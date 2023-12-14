package fi.methics.musap.sdk.internal.datatype;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

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
    private static final String POLL_MSG_TYPE         = "getdata";
    private static final String SIG_CALLBACK_MSG_TYPE = "signaturecallback";
    private static final String SIGN_MSG_TYPE         = "externalsignature";


    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(byte[].class, new ByteaMarshaller()).create();

    private String url;
    private String id;

    private String aesKey;
    private String macKey;

    public MusapLink(String url, String id) {
        this.url = url;
        this.id  = id;
    }

    /**
     * Set MUSAP ID
     * @param id MUSAP ID
     */
    public void setMusapId(String id) {
        this.id = id;
    }

    /**
     * Get MUSAP ID
     * @return MUSAP ID
     */
    public String getMusapId() {
        return this.id;
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
        OkHttpClient client = new OkHttpClient.Builder().build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String sResp = response.body().string();
                MLog.d("Got response " + sResp);

                MusapMessage respMsg = GSON.fromJson(sResp, MusapMessage.class);
                MLog.d("Response payload=" + respMsg.payload);
                String payloadJson = new String(Base64.decode(respMsg.payload, Base64.NO_WRAP));
                MLog.d("Decoded=" + payloadJson);
                EnrollDataResponsePayload respPayload = GSON.fromJson(payloadJson, EnrollDataResponsePayload.class);

                this.id = respPayload.getMusapId();
                return this;
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
        CouplingPayload payload = new CouplingPayload(couplingCode, uuid);

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
        OkHttpClient client = new OkHttpClient.Builder().build();
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

                CouplingResponsePayload resp = GSON.fromJson(payloadJson, CouplingResponsePayload.class);
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
    public PollResp poll() throws IOException {
        MusapMessage msg = new MusapMessage();
        msg.type = POLL_MSG_TYPE;
        msg.musapId = this.id;
        MLog.d("Message=" + msg.toJson());
        MLog.d("Url=" + this.url);

        RequestBody body = RequestBody.create(msg.toJson(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(this.url)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient.Builder().build();
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

                return new PollResp(payload, transId);
            } else {
                MLog.d("Null response");
                return null;
            }
        }
    }

    /**
     * Send a signature callback to MUSAP Link.
     * This performs networking operations.
     *
     * @param signature MusapSignature
     * @param transId
     * @throws IOException
     */
    public void sendSignatureCallback(MusapSignature signature, String transId) throws IOException {

        SignatureCallbackPayload payload = new SignatureCallbackPayload(null, signature);

        MusapMessage msg = new MusapMessage();
        msg.type = SIG_CALLBACK_MSG_TYPE;
        msg.payload = payload.toBase64();
        msg.musapId = this.id;
        msg.transid = transId;
        MLog.d("Message=" + msg.toJson());
        MLog.d("Url=" + this.url);

        RequestBody body = RequestBody.create(msg.toJson(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(this.url)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient.Builder().build();
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
     * Request external Signature with the "externalsignature" Coupling API
     * @param payload External Signature request payload
     * @return External Signature response payload
     * @throws IOException
     */
    public ExternalSignatureResponsePayload sign(ExternalSignaturePayload payload) throws IOException {

        MusapMessage msg = new MusapMessage();
        msg.payload = payload.toBase64();
        msg.type    = SIGN_MSG_TYPE;
        MLog.d("Message=" + msg.toJson());

        RequestBody body = RequestBody.create(msg.toJson(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(this.url)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient.Builder().build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String sResp = response.body().string();
                MLog.d("Got response " + sResp);

                MusapMessage respMsg = GSON.fromJson(sResp, MusapMessage.class);
                MLog.d("Response payload=" + respMsg.payload);
                String payloadJson = new String(Base64.decode(respMsg.payload, Base64.NO_WRAP));
                MLog.d("Decoded=" + payloadJson);

                ExternalSignatureResponsePayload resp = GSON.fromJson(payloadJson, ExternalSignatureResponsePayload.class);
                MLog.d("Parsed payload");
                if (resp.isSuccess()) {
                    return resp;
                } else {
                    return null;
                }
            } else {
                MLog.d("Null response");
                return null;
            }
        }
    }

}
