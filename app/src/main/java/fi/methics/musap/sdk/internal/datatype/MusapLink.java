package fi.methics.musap.sdk.internal.datatype;

import com.google.gson.Gson;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.sscd.methicsdemo.MethicsDemoSscd;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MusapLink {

    public static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

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

    public void couple(String couplingCode) throws IOException {
        // TODO: This must be stored
        String uuid = UUID.randomUUID().toString();

        CouplingPayload payload = new CouplingPayload(couplingCode, uuid);

        MusapMessage msg = new MusapMessage();
        msg.payload = payload.toBase64();
        msg.type = "linkaccount";
        MLog.d("Message=" + msg.toJson());

        RequestBody body = RequestBody.create(new Gson().toJson(msg.toJson()), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(this.url)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient.Builder().build();
        try (Response response = client.newCall(request).execute()) {

            if (response.body() != null) {
                String sResp = response.body().string();
                MLog.d("Got response " + sResp);
            } else {
                MLog.d("Null response");
            }
        }
    }
}
