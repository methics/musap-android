package fi.methics.musap.sdk.sscd.methicsdemo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.extension.MusapSscdInterface;
import fi.methics.musap.sdk.internal.datatype.CmsSignature;
import fi.methics.musap.sdk.internal.datatype.KeyAlgorithm;
import fi.methics.musap.sdk.internal.datatype.KeyURI;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapLoA;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.datatype.MusapSscd;
import fi.methics.musap.sdk.internal.datatype.SignatureFormat;
import fi.methics.musap.sdk.internal.discovery.KeyBindReq;
import fi.methics.musap.sdk.internal.keygeneration.KeyGenReq;
import fi.methics.musap.sdk.internal.sign.SignatureReq;
import fi.methics.musap.sdk.internal.util.MBase64;
import fi.methics.musap.sdk.internal.util.MLog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * MUSAP implementation for demo RemoteSignature SSCD that uses Methics demo at https://mobileid.dev
 */
public class MethicsDemoSscd implements MusapSscdInterface<MethicsDemoSettings> {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final String SSCD_TYPE         = "Methics Demo";
    public static final String ATTRIBUTE_MSISDN  = "msisdn";

    private static final int POLL_AMOUNT = 10;

    private static final Gson GSON = new Gson();

    private Context             context;
    private MethicsDemoSettings settings;
    private OkHttpClient        client;

    public MethicsDemoSscd(Context context, MethicsDemoSettings settings) {
        this.context  = context;
        this.settings = settings;
        this.client   = new OkHttpClient.Builder().readTimeout(settings.getTimeout()).build();
    }

    @Override
    public MusapKey bindKey(KeyBindReq req) throws Exception {
        CompletableFuture<DemoBindResult> future = new CompletableFuture<>();
        openKeygenPopup(req, future);

        DemoBindResult result = future.get();
        if (result.key       != null) return result.key;
        if (result.exception != null) throw result.exception;

        throw new MusapException("Bind failed");
    }

    @Override
    public MusapKey generateKey(KeyGenReq req) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MusapSignature sign(SignatureReq req) throws Exception {
        DemoSigReq jReq = new DemoSigReq();
        jReq.msisdn  = req.getKey().getAttributeValue(ATTRIBUTE_MSISDN);
        jReq.message = req.getDisplayText();
        jReq.dtbs    = MBase64.toBase64String(req.getData());
        jReq.async   = true;

        if (jReq.msisdn == null) throw new MusapException(MusapException.ERROR_MISSING_PARAM, "Missing MSISDN");
        if (jReq.dtbs   == null) throw new MusapException(MusapException.ERROR_MISSING_PARAM, "Missing DTBS");

        RequestBody body = RequestBody.create(GSON.toJson(jReq), JSON);
        Request request = new Request.Builder()
                .url(this.settings.getDemoUrl() + jReq.msisdn)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {

            MLog.d("Sending request " + GSON.toJson(jReq));
            String sResp = response.body().string();
            MLog.d("Got response " + sResp);

            DemoSigResp jResp = GSON.fromJson(sResp, DemoSigResp.class);
            return pollForSignature(jReq.msisdn, jResp.transid, "fetch");
        }
    }

    @Override
    public MusapSscd getSscdInfo() {
        return new MusapSscd.Builder()
                .setSscdName(this.settings.getSscdName())
                .setSscdType(SSCD_TYPE)
                .setCountry("FI")
                .setProvider("Methics")
                .setKeygenSupported(false)
                .setSupportedAlgorithms(Arrays.asList(KeyAlgorithm.RSA_2K))
                .setSupportedFormats(Arrays.asList(SignatureFormat.RAW, SignatureFormat.CMS))
                .build();
    }

    @Override
    public String generateSscdId(MusapKey key) {
        return SSCD_TYPE + "/" + key.getAttributeValue(ATTRIBUTE_MSISDN);
    }

    @Override
    public MethicsDemoSettings getSettings() {
        return settings;
    }

    private void openKeygenPopup(KeyBindReq req, CompletableFuture<DemoBindResult> future) {

        PopupWindow popupWindow = new PopupWindow(context);
        TextView   popupContent = new TextView(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        popupContent.setText("Please enter MSISDN");
        popupContent.setBackgroundColor(Color.LTGRAY);
        popupContent.setPadding(20, 20, 20, 20);

        EditText msisdnEditText = new EditText(context);
        msisdnEditText.setHint("Enter MSISDN");
        msisdnEditText.setInputType(InputType.TYPE_CLASS_PHONE);

        Button button = new Button(context);
        button.setText("Bind Key");
        button.setOnClickListener(v -> {
            CompletableFuture.runAsync(() -> {
                try {
                    MusapKey key = _bindKey(req, msisdnEditText.getText().toString());
                    future.complete(new DemoBindResult(key));
                } catch (MusapException e) {
                    MLog.e("Failed to bind key", e);
                    future.complete(new DemoBindResult(e));
                }
            });
            popupWindow.dismiss();
        });

        layout.addView(popupContent);
        layout.addView(msisdnEditText);
        layout.addView(button);

        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(Color.LTGRAY);
        backgroundDrawable.setCornerRadius(16);

        popupWindow.setBackgroundDrawable(backgroundDrawable);

        popupWindow.setContentView(layout);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        req.getActivity().runOnUiThread(() -> popupWindow.showAtLocation(req.getView(), Gravity.CENTER, 0, 0));
    }

    private MusapKey _bindKey(KeyBindReq req, String msisdn) throws MusapException {

        MLog.d("Sending keygen request to Methics demo for MSISDN " + msisdn);

        DemoSigReq jReq = new DemoSigReq();
        jReq.msisdn  = msisdn;
        jReq.message = "Activate MUSAP";
        jReq.async   = true;

        if (jReq.msisdn == null) throw new MusapException(MusapException.ERROR_MISSING_PARAM, "Missing MSISDN");

        try {
            RequestBody body = RequestBody.create(GSON.toJson(jReq), JSON);
            Request request = new Request.Builder()
                    .url(this.getSettings().getDemoUrl() + jReq.msisdn)
                    .post(body)
                    .build();
            MLog.d("Sending request " + GSON.toJson(jReq));
            try (Response response = client.newCall(request).execute()) {

                String sResp = response.body().string();
                MLog.d("Got response " + sResp);

                DemoSigResp jResp = GSON.fromJson(sResp, DemoSigResp.class);
                CmsSignature signature = pollForSignature(msisdn, jResp.transid, "fetch");
                MLog.d("Successfully bound Methics Demo SSCD");
                MusapKey.Builder builder = new MusapKey.Builder();
                builder.setCertificate(signature.getSignerCertificate());
                builder.setKeyName(req.getKeyAlias());
                builder.setSscdType(SSCD_TYPE);
                builder.setSscdId(this.getSscdInfo().getSscdId());
                builder.setLoa(Arrays.asList(MusapLoA.EIDAS_SUBSTANTIAL, MusapLoA.ISO_LOA3));
                builder.addAttribute(ATTRIBUTE_MSISDN, msisdn);
                return builder.build();
            }
        } catch (Exception e) {
            throw new MusapException(e);
        }
    }

    private CmsSignature pollForSignature(String msisdn, String transid, String type) throws IOException, MusapException {
        DemoSigReq jReq = new DemoSigReq();
        jReq.msisdn  = msisdn;
        jReq.transid = transid;
        jReq.type    = type;

        RequestBody body = RequestBody.create(GSON.toJson(jReq), JSON);
        Request request = new Request.Builder()
                .url(this.getSettings().getDemoUrl() + jReq.msisdn)
                .post(body)
                .build();

        for (int i = 0; i < POLL_AMOUNT; i++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                continue;
            }
            try (Response response = client.newCall(request).execute()) {

                MLog.d("Sending request " + GSON.toJson(jReq));
                String sResp = response.body().string();
                MLog.d("Got response " + sResp);

                DemoSigResp jResp = GSON.fromJson(sResp, DemoSigResp.class);

                if (jResp.success == false && (jResp.statuscode == null || "0".equals(jResp.statuscode))) {
                    // No response yet
                    MLog.d("Not ready yet");
                    continue;
                }

                if ("500".equals(jResp.statuscode)) {
                    MLog.d("Successfully signed");
                } else {
                    throw this.handleError(jResp.statuscode);
                }

                return new CmsSignature(MBase64.toBytes(jResp.signature));
            }
        }
        throw new MusapException(MusapException.ERROR_TIMED_OUT, "Failed to get a signature in " + POLL_AMOUNT + " attempts");
    }

    private MusapException handleError(String statusCode) {
        switch (statusCode) {
            case "105": return new MusapException(MusapException.ERROR_UNKNOWN_KEY, "No such user");
            case "208": return new MusapException(MusapException.ERROR_TIMED_OUT,   "Timed out");
            case "401": return new MusapException(MusapException.ERROR_USER_CANCEL, "User cancelled");
            case "402": return new MusapException(MusapException.ERROR_KEY_BLOCKED, "PIN blocked");
            case "403": return new MusapException(MusapException.ERROR_SSCD_BLOCKED, "SSCD blocked");
            case "425": return new MusapException(MusapException.ERROR_SSCD_BLOCKED, "Certificate validation failed");
            default: return new MusapException(MusapException.ERROR_INTERNAL, "Failed with status " + statusCode);
        }
    }

    private static class DemoBindResult {

        public MusapKey key;
        public MusapException exception;

        public DemoBindResult(MusapKey key) {
            this.key = key;
        }

        public DemoBindResult(MusapException e) {
            this.exception = e;
        }

    }

    private static class DemoSigReq {

        public String type = "sign";
        public String msisdn;
        public String message;
        public String transid;
        public String dtbs;
        public String mimetype;
        public String encoding;
        public boolean async;

    }

    private static class DemoSigResp {

        public String type;
        public String statuscode;
        public String signature;
        public String transid;
        public boolean success;

    }

}
