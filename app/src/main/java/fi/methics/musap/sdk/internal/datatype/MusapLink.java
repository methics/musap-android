package fi.methics.musap.sdk.internal.datatype;

import android.util.Base64;

import com.google.gson.Gson;

import java.io.IOException;

import fi.methics.musap.sdk.internal.util.MLog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MusapLink {

    public static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private static final String COUPLE_MSG_TYPE = "linkaccount";
    private static final String ENROLL_MSG_TYPE = "enrolldata";

    private static final Gson GSON = new Gson();

    private String url;
    private String id;

    private String aesKey;
    private String macKey;

    public MusapLink(String url, String id) {
        this.url = url;
        this.id  = id;
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
     * @return ID for this instance. Null if enroll failed.
     * @throws IOException
     */
    public String enroll(String fcmToken) throws IOException {
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

                return respPayload.getMusapId();
            } else {
                MLog.d("Null response");
                return null;
            }
        }
    }

    /**
     * Couple this MUSAP with a MUSAP Link.
     * This performs networking operations.
     * @return True if pairing was a success.
     * @throws IOException
     */
    public boolean couple(String couplingCode, String uuid) throws IOException {
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
                MLog.d("Response payload=" + respMsg.payload);
                String payloadJson = new String(Base64.decode(respMsg.payload, Base64.NO_WRAP));
                MLog.d("Decoded=" + payloadJson);

                CouplingResponsePayload resp = GSON.fromJson(payloadJson, CouplingResponsePayload.class);
                MLog.d("Parsed payload");
                return resp.isSuccess();
            } else {
                MLog.d("Null response");
                return false;
            }
        }
    }
}
