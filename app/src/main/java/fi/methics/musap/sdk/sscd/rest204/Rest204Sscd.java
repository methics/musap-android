package fi.methics.musap.sdk.sscd.rest204;

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
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
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
import fi.methics.musap.sdk.internal.util.IdGenerator;
import fi.methics.musap.sdk.internal.util.MBase64;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.sscd.rest204.json.MSS_Resp;
import fi.methics.musap.sdk.sscd.rest204.json.MSS_SignatureReq;
import fi.methics.musap.sdk.sscd.rest204.json.MSS_SignatureResp;
import fi.methics.musap.sdk.sscd.rest204.json.MSS_StatusReq;
import fi.methics.musap.sdk.sscd.rest204.json.MSS_StatusResp;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Rest204Sscd implements MusapSscdInterface<Rest204Settings> {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static final String SSCD_TYPE         = "204 REST";
    public static final String ATTRIBUTE_MSISDN  = "msisdn";
    public static final String ATTRIBUTE_NOSPAM  = "nospamcode";
    public static final String ATTRIBUTE_EVENTID = "eventid";
    public static final String ATTRIBUTE_SSCD    = "sscdname";

    private static final Gson GSON = new Gson();
    private static final int POLL_AMOUNT = 20;

    private Context         context;
    private Rest204Settings settings;
    private OkHttpClient    client;
    private RestHmac        hmacClient;

    public Rest204Sscd(Context context, Rest204Settings settings) {
        this.context  = context;
        this.settings = settings;
        this.client   = new OkHttpClient.Builder().readTimeout(settings.getTimeout()).build();
        this.hmacClient = new RestHmac(this.client, this.settings.getRestUserId(), this.settings.getRestApiKey());
    }

    @Override
    public MusapKey bindKey(KeyBindReq req) throws Exception {
        CompletableFuture<RestBindResult> future = new CompletableFuture<>();
        openKeygenPopup(req, future);

        RestBindResult result = future.get();
        if (result.key       != null) return result.key;
        if (result.exception != null) throw result.exception;

        throw new MusapException("Bind failed");
    }

    @Override
    public MusapKey generateKey(KeyGenReq req) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MusapSignature sign(SignatureReq req) throws Exception {

        MusapKey musapKey = req.getKey();
        String msisdn = musapKey.getAttributeValue(ATTRIBUTE_MSISDN);
        MLog.d("Sending signature request to REST 204 for MSISDN " + msisdn);

        MSS_SignatureReq jReq = new MSS_SignatureReq(msisdn);
        jReq.dtbd             = new MSS_SignatureReq.DTBD(req.getDisplayText());
        jReq.dtbs             = new MSS_SignatureReq.DTBS(req.getData());
        jReq.signatureProfile = this.settings.getSignatureProfile();

        if (!this.settings.isDtbdEnabled()) {
            jReq.dtbd = null;
        }
        if (req.getFormat() == null || SignatureFormat.RAW.equals(req.getFormat())) {
            jReq.format = this.getSettings().getRawFormatUri();
        } else {
            jReq.format = this.getSettings().getCmsFormatUri();
        }

        if (this.settings.isNoSpamEnabled()) {
            jReq.additionalServices = new ArrayList<>();
            MSS_SignatureReq.AdditionalService nospam = new MSS_SignatureReq.AdditionalService();
            nospam.description = "http://mss.ficom.fi/TS102204/v1.0.0#noSpam";
            nospam.noSpamCode  = new MSS_SignatureReq.NoSpamCode();
            nospam.noSpamCode.code = req.getAttribute(ATTRIBUTE_NOSPAM);
            jReq.additionalServices.add(nospam);
        }

        if (msisdn == null) throw new MusapException(MusapException.ERROR_MISSING_PARAM, "Missing MSISDN");

        try {
            String restUrl = this.getSettings().getRestUrl();
            URL        url = new URL(restUrl);

            String             json = "{\"MSS_SignatureReq\": " + GSON.toJson(jReq) + "}";
            RequestBody        body = RequestBody.create(json, JSON);
            Headers.Builder headers = new Headers.Builder();

            Map<String, String> headersToAdd = this.hmacClient.requestHeaders("POST", url, json);
            for (String key : headersToAdd.keySet()) {
                headers.add(key, headersToAdd.get(key));
            }

            Request request = new Request.Builder()
                    .url(restUrl)
                    .headers(headers.build())
                    .post(body)
                    .build();

            MLog.d("Sending request " + GSON.toJson(jReq));
            try (Response response = client.newCall(request).execute()) {

                String sResp = response.body().string();
                MLog.d("Got response " + sResp);

                MSS_Resp resp = GSON.fromJson(sResp, MSS_Resp.class);
                MSS_SignatureResp jResp = resp.signatureResp;

                if (resp.fault != null) {
                    throw this.handleError(resp.fault.code.subCode.value);
                }
                MusapSignature signature = pollForSignature(req.getFormat(), jResp);
                signature.setKey(req.getKey());
                return signature;
            }
        } catch (Exception e) {
            throw new MusapException(e);
        }
    }

    @Override
    public MusapSscd getSscdInfo() {
        return new MusapSscd.Builder()
                .setSscdType(SSCD_TYPE)
                .setCountry(this.getSettings().getCountry())
                .setSscdName(this.getSettings().getSscdName())
                .setKeygenSupported(false)
                .setSupportedAlgorithms(Arrays.asList(KeyAlgorithm.RSA_2K))
                .setSupportedFormats(Arrays.asList(SignatureFormat.RAW, SignatureFormat.CMS))
                .setProvider(this.getSettings().getProvider())
                .build();
    }

    @Override
    public String generateSscdId(MusapKey key) {
        return SSCD_TYPE + "/" + key.getAttributeValue(ATTRIBUTE_MSISDN);
    }

    @Override
    public Rest204Settings getSettings() {
        return this.settings;
    }

    private void openKeygenPopup(KeyBindReq req, CompletableFuture<RestBindResult> future) {

        PopupWindow popupWindow = new PopupWindow(context);
        TextView popupContent = new TextView(context);
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
            MLog.d("Bind Key pressed");
            CompletableFuture.runAsync(() -> {
                try {
                    MusapKey key = _bindKey(req, msisdnEditText.getText().toString());
                    future.complete(new RestBindResult(key));
                } catch (MusapException e) {
                    MLog.e("Failed to bind key", e);
                    future.complete(new RestBindResult(e));
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

        MLog.d("Sending bind request to REST 204 for MSISDN " + msisdn);

        MSS_SignatureReq jReq = new MSS_SignatureReq(msisdn);
        jReq.dtbd             = new MSS_SignatureReq.DTBD(req.getDisplayText());
        jReq.dtbs             = new MSS_SignatureReq.DTBS(jReq.dtbd);
        jReq.signatureProfile = this.settings.getBindSignatureProfile();
        jReq.format           = this.getSettings().getCmsFormatUri();

        if (!this.settings.isDtbdEnabled()) {
            jReq.dtbd = null;
        }
        if (this.settings.isNoSpamEnabled()) {
            jReq.additionalServices = new ArrayList<>();
            MSS_SignatureReq.AdditionalService nospam = new MSS_SignatureReq.AdditionalService();
            nospam.description = "http://mss.ficom.fi/TS102204/v1.0.0#noSpam";
            nospam.noSpamCode  = new MSS_SignatureReq.NoSpamCode();
            nospam.noSpamCode.code = req.getAttribute(ATTRIBUTE_NOSPAM);
            jReq.additionalServices.add(nospam);
        }

        if (msisdn == null) throw new MusapException(MusapException.ERROR_MISSING_PARAM, "Missing MSISDN");

        try {
            String restUrl = this.getSettings().getRestUrl();
            URL        url = new URL(restUrl);

            String             json = "{\"MSS_SignatureReq\": " + GSON.toJson(jReq) + "}";
            RequestBody        body = RequestBody.create(json, JSON);
            Headers.Builder headers = new Headers.Builder();

            Map<String, String> headersToAdd = this.hmacClient.requestHeaders("POST", url, json);
            for (String key : headersToAdd.keySet()) {
                headers.add(key, headersToAdd.get(key));
            }

            Request request = new Request.Builder()
                    .url(restUrl)
                    .headers(headers.build())
                    .post(body)
                    .build();

            MLog.d("Sending request " + GSON.toJson(jReq));
            try (Response response = client.newCall(request).execute()) {

                String sResp = response.body().string();
                MLog.d("Got response " + sResp);

                MSS_Resp resp = GSON.fromJson(sResp, MSS_Resp.class);
                MSS_SignatureResp jResp = resp.signatureResp;

                if (resp.fault != null) {
                    throw this.handleError(resp.fault.code.subCode.value);
                }

                CmsSignature signature = (CmsSignature) pollForSignature(SignatureFormat.CMS, jResp);
                MLog.d("Successfully bound REST 204 SSCD");
                MusapKey.Builder builder = new MusapKey.Builder();
                builder.setCertificate(signature.getSignerCertificate());
                builder.setKeyName(req.getKeyAlias());
                builder.setSscdType(SSCD_TYPE);
                builder.setSscdId(this.getSscdInfo().getSscdId());
                builder.setLoa(Arrays.asList(MusapLoA.EIDAS_SUBSTANTIAL, MusapLoA.ISO_LOA3));
                builder.addAttribute(ATTRIBUTE_MSISDN, msisdn);
                builder.setKeyId(IdGenerator.generateKeyId());
                return builder.build();
            }
        } catch (Throwable e) {
            MLog.d("Failed to bind MSISDN " + msisdn + ": " + e.getMessage());
            throw new MusapException(new Exception(e));
        }
    }

    private MusapSignature pollForSignature(SignatureFormat format, MSS_SignatureResp sigResp) throws IOException, MusapException {
        MSS_StatusReq jReq = new MSS_StatusReq(sigResp);

        String restUrl = this.getSettings().getRestUrl();
        URL        url = new URL(restUrl);

        String             json = "{ \"MSS_StatusReq\": " + GSON.toJson(jReq) + "}";
        RequestBody        body = RequestBody.create(json, JSON);
        Headers.Builder headers = new Headers.Builder();

        Map<String, String> headersToAdd = this.hmacClient.requestHeaders("POST", url, json);
        for (String key : headersToAdd.keySet()) {
            headers.add(key, headersToAdd.get(key));
        }

        Request request = new Request.Builder()
                .url(this.getSettings().getRestUrl())
                .headers(headers.build())
                .post(body)
                .build();

        for (int i = 0; i < POLL_AMOUNT; i++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                MLog.d("Poll interrupted");
                continue;
            }
            try (Response response = client.newCall(request).execute()) {

                MLog.d("Sending request " + GSON.toJson(jReq));
                String sResp = response.body().string();
                MLog.d("Got response " + sResp);

                MSS_Resp resp = GSON.fromJson(sResp, MSS_Resp.class);
                MSS_StatusResp jResp = resp.statusResp;

                if (resp.fault != null) {
                    throw this.handleError(resp.fault.code.subCode.value);
                }

                if (jResp.status != null && (jResp.status.statusCode == null || "504".equals(jResp.status.statusCode.value))) {
                    // No response yet
                    MLog.d("Not ready yet");
                    continue;
                }

                switch (jResp.status.statusCode.value) {
                    case "500":
                    case "502":
                        MLog.d("Successfully signed");
                        break;
                    default:
                        throw this.handleError(jResp.status.statusCode.value);
                }
                if (SignatureFormat.CMS.equals(format)) {
                    return new CmsSignature(MBase64.toBytes(jResp.signature.base64Signature));
                } else {
                    return new MusapSignature(MBase64.toBytes(jResp.signature.base64Signature));
                }
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

    private static class RestBindResult {

        public MusapKey key;
        public MusapException exception;

        public RestBindResult(MusapKey key) {
            this.key = key;
        }

        public RestBindResult(MusapException e) {
            this.exception = e;
        }

    }

}
