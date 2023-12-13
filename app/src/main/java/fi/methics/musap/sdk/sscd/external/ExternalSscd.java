package fi.methics.musap.sdk.sscd.external;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.util.Base64;
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
import fi.methics.musap.sdk.internal.datatype.CouplingPayload;
import fi.methics.musap.sdk.internal.datatype.CouplingResponsePayload;
import fi.methics.musap.sdk.internal.datatype.ExternalSignaturePayload;
import fi.methics.musap.sdk.internal.datatype.ExternalSignatureResponsePayload;
import fi.methics.musap.sdk.internal.datatype.KeyAlgorithm;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapLink;
import fi.methics.musap.sdk.internal.datatype.MusapLoA;
import fi.methics.musap.sdk.internal.datatype.MusapMessage;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.datatype.MusapSscd;
import fi.methics.musap.sdk.internal.datatype.RelyingParty;
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
 * SSCD that uses MUSAP Link to request signatures with the "externalsign" Coupling API call
 */
public class ExternalSscd implements MusapSscdInterface<ExternalSscdSettings> {

    public static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    public static final String SSCD_TYPE         = "External Signature";
    public static final String ATTRIBUTE_MSISDN  = "msisdn";
    public static final String SIGN_MSG_TYPE     = "externalsignature";

    private static final int POLL_AMOUNT = 10;

    private static final Gson GSON = new Gson();

    private Context              context;
    private ExternalSscdSettings settings;
    private OkHttpClient         client;
    private MusapLink            musapLink;

    private String clientid;

    public ExternalSscd(Context context, ExternalSscdSettings settings) {
        this.context   = context;
        this.settings  = settings;
        this.musapLink = settings.getMusapLink();
        this.client    = new OkHttpClient.Builder().readTimeout(settings.getTimeout()).build();
    }

    @Override
    public MusapKey bindKey(KeyBindReq req) throws Exception {

        ExternalSignaturePayload         request  = new ExternalSignaturePayload(this.clientid);
        ExternalSignatureResponsePayload response = this.musapLink.sign(request);

        CmsSignature signature = new CmsSignature(response.getRawSignature());

        return new MusapKey.Builder()
                .setCertificate(signature.getSignerCertificate())
                .build();
    }

    @Override
    public MusapKey generateKey(KeyGenReq req) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MusapSignature sign(SignatureReq req) throws Exception {

        ExternalSignaturePayload         request  = new ExternalSignaturePayload(this.clientid);
        ExternalSignatureResponsePayload response = this.musapLink.sign(request);

        return new MusapSignature(response.getRawSignature());
    }

    @Override
    public MusapSscd getSscdInfo() {
        return new MusapSscd.Builder()
                .setSscdName(this.settings.getSscdName())
                .setSscdType(SSCD_TYPE)
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
    public ExternalSscdSettings getSettings() {
        return settings;
    }

}
